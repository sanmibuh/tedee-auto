package org.sanmibuh.tedee.lock.infrastructure.secondary;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.io.IOException;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.sanmibuh.tedee.lock.domain.InvalidLockRequestException;
import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockId;
import org.sanmibuh.tedee.lock.domain.LockOperationFailedException;
import org.sanmibuh.tedee.lock.domain.LockStatus;
import org.sanmibuh.tedee.lock.domain.LockTemporarilyUnavailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

@RestClientTest(TedeeLockRepository.class)
@Import(TedeeClientConfiguration.class)
@TestPropertySource(
    properties = {
      "sanmibuh.rest.tedee.base-url=" + TedeeLockRepositoryTest.BASE_URL,
      "sanmibuh.rest.tedee.api-key=" + TedeeLockRepositoryTest.API_KEY
    })
class TedeeLockRepositoryTest {

  static final String BASE_URL = "http://localhost/v1.0";
  static final String API_KEY = "test-api-key";
  private static final int DEVICE_ID = 42;
  private static final String LOCK_URL = BASE_URL + "/lock/" + DEVICE_ID + "/lock";

  @Autowired private TedeeLockRepository sut;

  @Autowired private MockRestServiceServer server;

  static Stream<Arguments> bridgeErrorsToDomainExceptions() {
    return Stream.of(
        Arguments.of(HttpStatus.NOT_FOUND, InvalidLockRequestException.class),
        Arguments.of(HttpStatus.UNAUTHORIZED, LockOperationFailedException.class),
        Arguments.of(HttpStatus.BAD_REQUEST, LockOperationFailedException.class),
        Arguments.of(HttpStatus.METHOD_NOT_ALLOWED, LockTemporarilyUnavailableException.class),
        Arguments.of(HttpStatus.NOT_ACCEPTABLE, LockTemporarilyUnavailableException.class),
        Arguments.of(HttpStatus.INTERNAL_SERVER_ERROR, LockOperationFailedException.class),
        Arguments.of(HttpStatus.BAD_GATEWAY, LockTemporarilyUnavailableException.class),
        Arguments.of(HttpStatus.SERVICE_UNAVAILABLE, LockTemporarilyUnavailableException.class),
        Arguments.of(HttpStatus.GATEWAY_TIMEOUT, LockTemporarilyUnavailableException.class));
  }

  @Test
  void should_postToLockEndpoint_whenSavingLockedLock() {
    server
        .expect(requestTo(LOCK_URL))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withNoContent());

    sut.save(lockedLock());

    server.verify();
  }

  @Test
  void should_sendApiToken_whenSavingLockedLock() {
    server
        .expect(requestTo(LOCK_URL))
        .andExpect(header("api_token", API_KEY))
        .andRespond(withNoContent());

    sut.save(lockedLock());

    server.verify();
  }

  @ParameterizedTest
  @MethodSource("bridgeErrorsToDomainExceptions")
  void should_translateBridgeError_whenBridgeRespondsWithError(
      final HttpStatus status, final Class<? extends Throwable> expectedException) {
    server
        .expect(requestTo(LOCK_URL))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withStatus(status));

    thenThrownBy(() -> sut.save(lockedLock())).isInstanceOf(expectedException);
  }

  @Test
  void should_throwLockTemporarilyUnavailable_whenBridgeIsUnreachable() {
    server
        .expect(requestTo(LOCK_URL))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withException(new IOException("bridge unreachable")));

    thenThrownBy(() -> sut.save(lockedLock()))
        .isInstanceOf(LockTemporarilyUnavailableException.class);
  }

  @Test
  void should_returnLock_whenFindingById() {
    then(sut.findById(new LockId(DEVICE_ID))).isPresent();
  }

  private static Lock lockedLock() {
    final var lock = new Lock(new LockId(DEVICE_ID), LockStatus.UNLOCKED);
    lock.lock();
    return lock;
  }
}
