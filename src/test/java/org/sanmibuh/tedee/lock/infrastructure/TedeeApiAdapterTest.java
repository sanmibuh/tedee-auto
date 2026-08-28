package org.sanmibuh.tedee.lock.infrastructure;

import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.sanmibuh.tedee.lock.domain.InvalidLockRequestException;
import org.sanmibuh.tedee.lock.domain.LockId;
import org.sanmibuh.tedee.lock.domain.LockOperationFailedException;
import org.sanmibuh.tedee.lock.domain.LockTemporarilyUnavailableException;
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

  static Stream<Arguments> bridgeErrorsToDomainExceptions() {
    return Stream.of(
        Arguments.of(HttpStatus.NOT_FOUND, InvalidLockRequestException.class),
        Arguments.of(HttpStatus.UNAUTHORIZED, LockOperationFailedException.class),
        Arguments.of(HttpStatus.BAD_REQUEST, LockOperationFailedException.class),
        Arguments.of(HttpStatus.METHOD_NOT_ALLOWED, LockTemporarilyUnavailableException.class),
        Arguments.of(HttpStatus.NOT_ACCEPTABLE, LockTemporarilyUnavailableException.class));
  }

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

  @ParameterizedTest
  @MethodSource("bridgeErrorsToDomainExceptions")
  void should_translateBridgeError_whenBridgeRespondsWithError(
      final HttpStatus status, final Class<? extends Throwable> expectedException) {
    server
        .expect(requestTo("http://localhost/v1.0/lock/42/lock"))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withStatus(status));

    thenThrownBy(() -> sut.lock(new LockId(42))).isInstanceOf(expectedException);
  }
}
