package org.sanmibuh.tedee.lock.infrastructure.secondary;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.InstanceOfAssertFactories.STRING;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.tedee.bridge.client.api.CallbackApi;
import java.time.Clock;
import java.time.Instant;
import nl.altindag.log.LogCaptor;
import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.sanmibuh.tedee.ServerProperties;
import org.sanmibuh.tedee.lock.infrastructure.TedeeInfrastructureConfiguration;
import org.sanmibuh.tedee.lock.infrastructure.TedeeProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;

@RestClientTest
@ExtendWith(SoftAssertionsExtension.class)
@Import({TedeeClientConfiguration.class, TedeeInfrastructureConfiguration.class})
@TestPropertySource(
    properties = {
      "sanmibuh.rest.tedee.base-url=" + TedeeCallbackRegistrationTest.BASE_URL,
      "sanmibuh.rest.tedee.api-key=secret",
      "sanmibuh.rest.tedee.callback-secret=" + TedeeCallbackRegistrationTest.CALLBACK_SECRET,
      "sanmibuh.rest.tedee.callback-registration-interval=1s",
      "sanmibuh.rest.tedee.retry.max-retries=0",
      "sanmibuh.rest.tedee.retry.initial-interval=1",
      "sanmibuh.rest.tedee.retry.multiplier=1",
      "sanmibuh.rest.tedee.retry.max-interval=1"
    })
class TedeeCallbackRegistrationTest {

  static final String BASE_URL = "http://localhost/v1.0";
  static final String CALLBACK_SECRET = "callback-secret";
  private static final String CALLBACK_ENDPOINT = BASE_URL + "/callback";
  private static final String PUBLIC_URL = "http://automation.local:8080";
  private static final String CALLBACK_URL = PUBLIC_URL + "/tedee/events";
  private static final String FOREIGN_CALLBACK_URL = "http://other-system.local/hook";
  private static final int FOREIGN_ID = 1;
  private static final int EXISTING_ID = 5;
  private static final int REGISTERED_ID = 7;
  private static final int DUPLICATE_ID = 9;
  private static final String REGISTERED_RESPONSE = "{\"id\": " + REGISTERED_ID + "}";
  private static final Instant NOW = Instant.parse("2026-10-10T14:00:00Z");

  @Autowired private CallbackApi callbackApi;

  @Autowired private MockRestServiceServer server;

  @MockitoBean private Clock clock;

  @MockitoBean private TaskScheduler taskScheduler;

  @Autowired private TedeeProperties tedeeProperties;

  @InjectSoftAssertions private BDDSoftAssertions softly;

  @SuppressWarnings("NullAway.Init")
  private TedeeCallbackRegistration sut;

  private static String callbacks(final String... callbacks) {
    return "[" + String.join(",", callbacks) + "]";
  }

  private static String callback(final int id, final String url) {
    return """
        {"id": %d, "url": "%s", "method": "POST", "headers": []}"""
        .formatted(id, url);
  }

  @BeforeEach
  void setUp() {
    given(clock.instant()).willReturn(NOW);
    sut =
        new TedeeCallbackRegistration(
            callbackApi, new ServerProperties(PUBLIC_URL), tedeeProperties, clock, taskScheduler);
  }

  @Test
  void should_postCallbackToBridge_whenNoCallbackMatchesOurUrl() {
    expectListedCallbacks(callbacks(callback(FOREIGN_ID, FOREIGN_CALLBACK_URL)));
    expectRegisteredCallback();

    sut.start();

    server.verify();
  }

  @Test
  void should_registerCallbackWithoutDoubleSlash_whenPublicUrlEndsWithSlash() {
    sut =
        new TedeeCallbackRegistration(
            callbackApi,
            new ServerProperties(PUBLIC_URL + "/"),
            tedeeProperties,
            clock,
            taskScheduler);
    expectListedCallbacks(callbacks());
    expectRegisteredCallback();

    sut.start();

    server.verify();
  }

  @Test
  void should_notPostCallback_whenCallbackAlreadyMatchesOurUrl() {
    expectListedCallbacks(
        callbacks(callback(FOREIGN_ID, FOREIGN_CALLBACK_URL), callback(EXISTING_ID, CALLBACK_URL)));

    sut.start();

    server.verify();
  }

  @Test
  void should_deleteDuplicateCallbacks_whenSeveralMatchOurUrl() {
    expectListedCallbacks(
        callbacks(
            callback(FOREIGN_ID, FOREIGN_CALLBACK_URL),
            callback(EXISTING_ID, CALLBACK_URL),
            callback(DUPLICATE_ID, CALLBACK_URL)));
    expectDeletedCallback(DUPLICATE_ID);

    sut.start();

    server.verify();
  }

  @Test
  void should_deleteRegisteredCallback_whenStoppedAfterRegistering() {
    expectListedCallbacks(callbacks());
    expectRegisteredCallback();
    expectDeletedCallback(REGISTERED_ID);
    sut.start();

    sut.stop();

    server.verify();
  }

  @Test
  void should_deleteReusedCallback_whenStoppedAfterReusingExistingOne() {
    expectListedCallbacks(callbacks(callback(EXISTING_ID, CALLBACK_URL)));
    expectDeletedCallback(EXISTING_ID);
    sut.start();

    sut.stop();

    server.verify();
  }

  @Test
  void should_notCallBridge_whenStoppedWhileNotRunning() {
    sut.stop();

    server.verify();
  }

  @Test
  void should_reportRunning_whenStarted() {
    expectListedCallbacks(callbacks(callback(EXISTING_ID, CALLBACK_URL)));

    sut.start();

    then(sut.isRunning()).isTrue();
  }

  @Test
  void should_reportNotRunning_whenStopped() {
    expectListedCallbacks(callbacks(callback(EXISTING_ID, CALLBACK_URL)));
    expectDeletedCallback(EXISTING_ID);
    sut.start();

    sut.stop();

    then(sut.isRunning()).isFalse();
  }

  @Test
  void should_remainRunningWithoutCallback_whenBridgeFails() {
    server
        .expect(requestTo(CALLBACK_ENDPOINT))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

    try (final var logCaptor = LogCaptor.forClass(TedeeCallbackRegistration.class)) {
      softly.thenCode(sut::start).doesNotThrowAnyException();
      softly.then(sut.isRunning()).isTrue();
      softly.then(logCaptor.getWarnLogs()).singleElement(STRING).contains(CALLBACK_URL);
    }
  }

  @Test
  void should_registerCallback_whenBridgeRecoversAfterStartupFailure() {
    server
        .expect(requestTo(CALLBACK_ENDPOINT))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));
    expectListedCallbacks(callbacks());
    expectRegisteredCallback();

    sut.start();
    sut.reregister();

    server.verify();
    then(sut.isRunning()).isTrue();
  }

  @Test
  void should_retryUntilBridgeRecovers_whenRegistrationKeepsFailing() {
    final var retries = ArgumentCaptor.forClass(Runnable.class);
    server
        .expect(requestTo(CALLBACK_ENDPOINT))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));
    server
        .expect(requestTo(CALLBACK_ENDPOINT))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));
    expectListedCallbacks(callbacks());
    expectRegisteredCallback();

    sut.start();
    verify(taskScheduler)
        .schedule(retries.capture(), org.mockito.ArgumentMatchers.any(Instant.class));
    retries.getValue().run();
    verify(taskScheduler, times(2))
        .schedule(retries.capture(), org.mockito.ArgumentMatchers.any(Instant.class));
    retries.getAllValues().getLast().run();

    server.verify();
    then(sut.isRunning()).isTrue();
    verify(taskScheduler, times(2))
        .schedule(
            org.mockito.ArgumentMatchers.any(Runnable.class),
            org.mockito.ArgumentMatchers.any(Instant.class));
  }

  @Test
  void should_scheduleRetryAtConfiguredInterval_whenBridgeFails() {
    server
        .expect(requestTo(CALLBACK_ENDPOINT))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

    sut.start();

    verify(taskScheduler)
        .schedule(
            org.mockito.ArgumentMatchers.any(Runnable.class),
            org.mockito.ArgumentMatchers.eq(NOW.plusSeconds(1)));
  }

  @Test
  void should_notRegisterCallback_whenReregisteringAfterStopped() {
    server
        .expect(requestTo(CALLBACK_ENDPOINT))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

    sut.start();
    sut.stop();
    sut.reregister();

    server.verify();
    then(sut.isRunning()).isFalse();
  }

  @Test
  void should_stopWithoutFailing_whenBridgeFailsToDeleteCallback() {
    expectListedCallbacks(callbacks(callback(EXISTING_ID, CALLBACK_URL)));
    server
        .expect(requestTo(CALLBACK_ENDPOINT + "/" + EXISTING_ID))
        .andExpect(method(HttpMethod.DELETE))
        .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));
    sut.start();

    try (final var logCaptor = LogCaptor.forClass(TedeeCallbackRegistration.class)) {
      softly.thenCode(sut::stop).doesNotThrowAnyException();
      softly.then(sut.isRunning()).isFalse();
      softly.then(logCaptor.getWarnLogs()).singleElement(STRING).contains(CALLBACK_URL);
    }
  }

  private void expectRegisteredCallback() {
    server
        .expect(requestTo(CALLBACK_ENDPOINT))
        .andExpect(method(HttpMethod.POST))
        .andExpect(jsonPath("$.url").value(CALLBACK_URL))
        .andExpect(jsonPath("$.method").value("POST"))
        .andExpect(
            jsonPath("$.headers[0].header_name")
                .value("X-Tedee-Callback-Secret: " + CALLBACK_SECRET))
        .andRespond(withSuccess(REGISTERED_RESPONSE, MediaType.APPLICATION_JSON));
  }

  private void expectDeletedCallback(final int id) {
    server
        .expect(requestTo(CALLBACK_ENDPOINT + "/" + id))
        .andExpect(method(HttpMethod.DELETE))
        .andRespond(withNoContent());
  }

  private void expectListedCallbacks(final String callbacks) {
    server
        .expect(requestTo(CALLBACK_ENDPOINT))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(callbacks, MediaType.APPLICATION_JSON));
  }
}
