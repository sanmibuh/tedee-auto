package org.sanmibuh.tedee.lock.infrastructure;

import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import org.junit.jupiter.api.Test;
import org.sanmibuh.tedee.lock.domain.InvalidApiTokenException;
import org.sanmibuh.tedee.lock.domain.LockId;
import org.sanmibuh.tedee.lock.domain.LockNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

@RestClientTest(TedeeApiAdapter.class)
@Import(TedeeClientConfiguration.class)
@TestPropertySource(
    properties = {
      "sanmibuh.rest.tedee.base-url=http://localhost/v1.0",
      "sanmibuh.rest.tedee.api-key=test-api-key"
    })
class TedeeApiAdapterTest {

  @Autowired private TedeeApiAdapter sut;

  @Autowired private MockRestServiceServer server;

  @Test
  void should_postToLockEndpoint_whenLockingDevice() {
    server
        .expect(requestTo("http://localhost/v1.0/lock/42/lock"))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withNoContent());

    sut.lock(new LockId(42));

    server.verify();
  }

  @Test
  void should_sendApiToken_whenLockingDevice() {
    server
        .expect(requestTo("http://localhost/v1.0/lock/42/lock"))
        .andExpect(header("api_token", "test-api-key"))
        .andRespond(withNoContent());

    sut.lock(new LockId(42));

    server.verify();
  }

  @Test
  void should_throwInvalidApiToken_whenBridgeRespondsUnauthorized() {
    server
        .expect(requestTo("http://localhost/v1.0/lock/42/lock"))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

    thenThrownBy(() -> sut.lock(new LockId(42))).isInstanceOf(InvalidApiTokenException.class);
  }

  @Test
  void should_throwLockNotFound_whenBridgeRespondsNotFound() {
    server
        .expect(requestTo("http://localhost/v1.0/lock/42/lock"))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withStatus(HttpStatus.NOT_FOUND));

    thenThrownBy(() -> sut.lock(new LockId(42))).isInstanceOf(LockNotFoundException.class);
  }
}
