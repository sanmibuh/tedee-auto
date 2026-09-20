package org.sanmibuh.tedee.lock.infrastructure.secondary;

import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.io.IOException;
import java.time.Clock;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;

@RestClientTest(TedeeLockGateway.class)
@Import(TedeeClientConfiguration.class)
@TestPropertySource(
    properties = {
      "sanmibuh.rest.tedee.base-url=" + TedeeLockGatewayTest.BASE_URL,
      "sanmibuh.rest.tedee.api-key=" + TedeeLockGatewayTest.API_KEY,
      "sanmibuh.rest.tedee.retry.max-retries=" + TedeeLockGatewayTest.MAX_RETRIES,
      "sanmibuh.rest.tedee.retry.initial-interval=1",
      "sanmibuh.rest.tedee.retry.multiplier=1",
      "sanmibuh.rest.tedee.retry.max-interval=1"
    })
class TedeeLockGatewayTest {

  static final String BASE_URL = "http://localhost/v1.0";
  static final String API_KEY = "BE9xnPnGfVUS";
  static final int MAX_RETRIES = 2;
  private static final int EXPECTED_ATTEMPTS = MAX_RETRIES + 1;
  private static final long FIXED_MILLIS = 1691058833000L;
  private static final String EXPECTED_API_TOKEN =
      "e59d9763edc6e59f2faccf9a769e5cf170d68439c3fd67afae5e3e72d0463a711691058833000";
  private static final int DEVICE_ID = 42;
  private static final String LOCK_URL = BASE_URL + "/lock/" + DEVICE_ID + "/lock";

  @Autowired private LockGateway sut;

  @Autowired private MockRestServiceServer server;

  @MockitoBean private Clock clock;

  static Stream<Arguments> nonTransientBridgeErrors() {
    return Stream.of(
        Arguments.of(HttpStatus.NOT_FOUND, InvalidLockRequestException.class),
        Arguments.of(HttpStatus.UNAUTHORIZED, LockOperationFailedException.class),
        Arguments.of(HttpStatus.BAD_REQUEST, LockOperationFailedException.class),
        Arguments.of(HttpStatus.INTERNAL_SERVER_ERROR, LockOperationFailedException.class));
  }

  static Stream<Arguments> transientBridgeErrors() {
    return Stream.of(
        Arguments.of(HttpStatus.METHOD_NOT_ALLOWED),
        Arguments.of(HttpStatus.NOT_ACCEPTABLE),
        Arguments.of(HttpStatus.BAD_GATEWAY),
        Arguments.of(HttpStatus.SERVICE_UNAVAILABLE),
        Arguments.of(HttpStatus.GATEWAY_TIMEOUT));
  }

  @Test
  void should_postToLockEndpoint_whenLocking() {
    server
        .expect(requestTo(LOCK_URL))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withNoContent());

    sut.lock(new LockId(DEVICE_ID));

    server.verify();
  }

  @Test
  void should_sendApiToken_whenLocking() {
    given(clock.millis()).willReturn(FIXED_MILLIS);
    server
        .expect(requestTo(LOCK_URL))
        .andExpect(header("api_token", EXPECTED_API_TOKEN))
        .andRespond(withNoContent());

    sut.lock(new LockId(DEVICE_ID));

    server.verify();
  }

  @ParameterizedTest
  @MethodSource("nonTransientBridgeErrors")
  void should_translateWithoutRetrying_whenBridgeRespondsWithNonTransientError(
      final HttpStatus status, final Class<? extends Throwable> expectedException) {
    server
        .expect(ExpectedCount.once(), requestTo(LOCK_URL))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withStatus(status));

    thenThrownBy(() -> sut.lock(new LockId(DEVICE_ID))).isInstanceOf(expectedException);

    server.verify();
  }

  @ParameterizedTest
  @MethodSource("transientBridgeErrors")
  void should_retryUpToConfiguredAttemptsThenGiveUp_whenBridgeKeepsRespondingWithTransientError(
      final HttpStatus status) {
    server
        .expect(ExpectedCount.times(EXPECTED_ATTEMPTS), requestTo(LOCK_URL))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withStatus(status));

    thenThrownBy(() -> sut.lock(new LockId(DEVICE_ID)))
        .isInstanceOf(LockTemporarilyUnavailableException.class);

    server.verify();
  }

  @Test
  void should_retryThenGiveUp_whenBridgeIsUnreachable() {
    server
        .expect(ExpectedCount.times(EXPECTED_ATTEMPTS), requestTo(LOCK_URL))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withException(new IOException("bridge unreachable")));

    thenThrownBy(() -> sut.lock(new LockId(DEVICE_ID)))
        .isInstanceOf(LockTemporarilyUnavailableException.class);

    server.verify();
  }

  @Test
  void should_retryUntilSuccess_whenBridgeRespondsWithTransientErrorThenSucceeds() {
    server
        .expect(requestTo(LOCK_URL))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));
    server
        .expect(requestTo(LOCK_URL))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withNoContent());

    sut.lock(new LockId(DEVICE_ID));

    server.verify();
  }
}
