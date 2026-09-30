package org.sanmibuh.tedee.lock.infrastructure.secondary;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.tedee.bridge.client.api.CallbackApi;
import java.time.Clock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sanmibuh.tedee.ServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;

@RestClientTest
@Import(TedeeClientConfiguration.class)
@TestPropertySource(
    properties = {
      "sanmibuh.rest.tedee.base-url=" + TedeeCallbackRegistrationTest.BASE_URL,
      "sanmibuh.rest.tedee.api-key=secret",
      "sanmibuh.rest.tedee.retry.max-retries=0",
      "sanmibuh.rest.tedee.retry.initial-interval=1",
      "sanmibuh.rest.tedee.retry.multiplier=1",
      "sanmibuh.rest.tedee.retry.max-interval=1"
    })
class TedeeCallbackRegistrationTest {

  static final String BASE_URL = "http://localhost/v1.0";
  private static final String CALLBACK_ENDPOINT = BASE_URL + "/callback";
  private static final String PUBLIC_URL = "http://automation.local:8080";
  private static final String CALLBACK_URL = PUBLIC_URL + "/tedee/events";
  private static final String FOREIGN_CALLBACK_URL = "http://other-system.local/hook";
  private static final String REGISTERED_RESPONSE = "{\"id\": 7}";

  @Autowired private CallbackApi callbackApi;

  @Autowired private MockRestServiceServer server;

  @MockitoBean private Clock clock;

  @SuppressWarnings("NullAway.Init")
  private TedeeCallbackRegistration sut;

  @BeforeEach
  void setUp() {
    sut = new TedeeCallbackRegistration(callbackApi, new ServerProperties(PUBLIC_URL));
  }

  @Test
  void should_postCallbackToBridge_whenNoCallbackMatchesOurUrl() {
    expectListedCallbacks("[" + callback(1, FOREIGN_CALLBACK_URL) + "]");
    server
        .expect(requestTo(CALLBACK_ENDPOINT))
        .andExpect(method(HttpMethod.POST))
        .andExpect(jsonPath("$.url").value(CALLBACK_URL))
        .andExpect(jsonPath("$.method").value("POST"))
        .andRespond(withSuccess(REGISTERED_RESPONSE, MediaType.APPLICATION_JSON));

    sut.start();

    server.verify();
  }

  @Test
  void should_notPostCallback_whenCallbackAlreadyMatchesOurUrl() {
    expectListedCallbacks(
        "[" + callback(1, FOREIGN_CALLBACK_URL) + "," + callback(7, CALLBACK_URL) + "]");

    sut.start();

    server.verify();
  }

  private void expectListedCallbacks(final String callbacks) {
    server
        .expect(requestTo(CALLBACK_ENDPOINT))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(callbacks, MediaType.APPLICATION_JSON));
  }

  private static String callback(final int id, final String url) {
    return """
        {"id": %d, "url": "%s", "method": "POST", "headers": []}"""
        .formatted(id, url);
  }
}
