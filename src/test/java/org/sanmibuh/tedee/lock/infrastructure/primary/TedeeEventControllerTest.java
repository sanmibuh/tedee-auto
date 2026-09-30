package org.sanmibuh.tedee.lock.infrastructure.primary;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.sanmibuh.ddd.port.CommandBus;
import org.sanmibuh.tedee.lock.application.ReportLockStatusCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TedeeEventController.class)
class TedeeEventControllerTest {

  private static final String EVENTS_PATH = "/tedee/events";

  @Autowired MockMvc sut;

  @MockitoBean CommandBus commandBus;

  @Test
  @SneakyThrows
  void should_dispatchReportLockStatusCommand_whenLockStatusChanged() {
    sut.perform(
            post(EVENTS_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "event": "lock-status-changed",
                      "timestamp": "2026-09-30T10:15:30.123Z",
                      "data": {
                        "deviceType": 2,
                        "deviceId": 33819,
                        "serialNumber": "19420103-000006",
                        "state": 6,
                        "jammed": 1,
                        "doorState": 2
                      }
                    }
                    """))
        .andExpect(status().isNoContent());

    verify(commandBus).dispatch(new ReportLockStatusCommand(33819, 6, true, 2));
  }
}
