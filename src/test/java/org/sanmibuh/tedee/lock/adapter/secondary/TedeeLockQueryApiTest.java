package org.sanmibuh.tedee.lock.adapter.secondary;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.time.Clock;
import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sanmibuh.tedee.shared.adapter.TedeeInfrastructureConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;

@ExtendWith(SoftAssertionsExtension.class)
@RestClientTest
@Import({TedeeClientConfiguration.class, TedeeInfrastructureConfiguration.class})
@TestPropertySource(
    properties = {
      "sanmibuh.rest.tedee.base-url=" + TedeeLockQueryApiTest.BASE_URL,
      "sanmibuh.rest.tedee.api-key=" + TedeeLockQueryApiTest.API_KEY,
      "sanmibuh.rest.tedee.callback-secret=callback-secret",
      "sanmibuh.rest.tedee.retry.max-retries=2",
      "sanmibuh.rest.tedee.retry.initial-interval=1",
      "sanmibuh.rest.tedee.retry.multiplier=1",
      "sanmibuh.rest.tedee.retry.max-interval=1"
    })
class TedeeLockQueryApiTest {

  static final String BASE_URL = "http://localhost/v1.0";
  static final String API_KEY = "BE9xnPnGfVUS";
  private static final long FIXED_MILLIS = 1691058833000L;
  private static final String EXPECTED_API_TOKEN =
      "e59d9763edc6e59f2faccf9a769e5cf170d68439c3fd67afae5e3e72d0463a711691058833000";
  private static final int DEVICE_ID = 42;
  private static final String LOCK_URL = BASE_URL + "/lock/" + DEVICE_ID;
  private static final String NAME = "Front door";

  @InjectSoftAssertions BDDSoftAssertions softly;

  @Autowired LockQueryApi sut;
  @Autowired MockRestServiceServer server;

  @MockitoBean Clock clock;

  @Test
  void should_returnLockDetails_whenRetrievingLock() {
    given(clock.millis()).willReturn(FIXED_MILLIS);
    server
        .expect(requestTo(LOCK_URL))
        .andExpect(method(HttpMethod.GET))
        .andExpect(header("api_token", EXPECTED_API_TOKEN))
        .andRespond(
            withSuccess(
                "{\"id\":%d,\"name\":\"%s\"}".formatted(DEVICE_ID, NAME),
                MediaType.APPLICATION_JSON));

    final var details = sut.getLock(DEVICE_ID);

    server.verify();
    softly.then(details.getId()).isEqualTo(DEVICE_ID);
    softly.then(details.getName()).isEqualTo(NAME);
  }
}
