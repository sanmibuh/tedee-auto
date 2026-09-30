package org.sanmibuh.tedee.lock.infrastructure.primary;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.InstanceOfAssertFactories.STRING;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sanmibuh.ddd.port.CommandBus;
import org.sanmibuh.tedee.lock.application.ReportLockStatusCommand;
import org.sanmibuh.tedee.lock.infrastructure.TedeeProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TedeeEventController.class)
class TedeeEventControllerTest {

  private static final String EVENTS_PATH = "/tedee/events";
  private static final String CALLBACK_SECRET_HEADER = "X-Tedee-Callback-Secret";
  static final String CALLBACK_SECRET = "callback-secret";

  @Autowired MockMvc sut;

  @MockitoBean CommandBus commandBus;

  @MockitoBean TedeeProperties tedeeProperties;

  @BeforeEach
  void setUp() {
    given(tedeeProperties.callbackSecret()).willReturn(CALLBACK_SECRET);
  }

  @Test
  @SneakyThrows
  void should_rejectEventWithoutDispatching_whenCallbackSecretIsMissing() {
    sut.perform(post(EVENTS_PATH).contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isUnauthorized());

    verifyNoInteractions(commandBus);
  }

  @Test
  @SneakyThrows
  void should_rejectEventWithoutDispatching_whenCallbackSecretIsIncorrect() {
    sut.perform(
            post(EVENTS_PATH)
                .header(CALLBACK_SECRET_HEADER, "incorrect-secret")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isUnauthorized());

    verifyNoInteractions(commandBus);
  }

  @Test
  @SneakyThrows
  void should_dispatchReportLockStatusCommand_whenLockStatusChanged() {
    sut.perform(
            post(EVENTS_PATH)
                .header(CALLBACK_SECRET_HEADER, CALLBACK_SECRET)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "event": "lock-status-changed",
                      "timestamp": "2023-07-25T14:41:48.825Z",
                      "data": {
                        "deviceType": 2,
                        "deviceId": 33819,
                        "serialNumber": "19420103-000006",
                        "state": 6,
                        "jammed": 0,
                        "doorState": 2
                      }
                    }
                    """))
        .andExpect(status().isNoContent());

    verify(commandBus).dispatch(new ReportLockStatusCommand(33819, 6, 0, 2));
  }

  @Test
  @SneakyThrows
  void should_acknowledgeWithoutDispatching_whenEventIsUnknown() {
    try (final var logCaptor = LogCaptor.forClass(TedeeEventController.class)) {
      sut.perform(
              post(EVENTS_PATH)
                  .header(CALLBACK_SECRET_HEADER, CALLBACK_SECRET)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      """
                      {
                        "event": "unknown-event",
                        "timestamp": "2023-07-25T14:41:48.825Z",
                        "data": {
                          "deviceType": 2,
                          "deviceId": 33819,
                          "serialNumber": "19420103-000006"
                        }
                      }
                      """))
          .andExpect(status().isNoContent());

      verifyNoInteractions(commandBus);
      then(logCaptor.getWarnLogs()).singleElement(STRING).contains("unknown-event");
    }
  }
}
