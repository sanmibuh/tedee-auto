package org.sanmibuh.tedee.lock.infrastructure.secondary;

import com.tedee.bridge.client.api.LockApi;
import lombok.RequiredArgsConstructor;
import org.sanmibuh.tedee.lock.domain.InvalidLockRequestException;
import org.sanmibuh.tedee.lock.domain.LockId;
import org.sanmibuh.tedee.lock.domain.LockOperationFailedException;
import org.sanmibuh.tedee.lock.domain.LockTemporarilyUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
@RequiredArgsConstructor
class TedeeLockGateway implements LockGateway {

  private final LockApi lockApi;

  @Override
  @Retryable(
      includes = LockTemporarilyUnavailableException.class,
      maxRetriesString = "${sanmibuh.rest.tedee.retry.max-retries}",
      delayString = "${sanmibuh.rest.tedee.retry.initial-interval}",
      multiplierString = "${sanmibuh.rest.tedee.retry.multiplier}",
      maxDelayString = "${sanmibuh.rest.tedee.retry.max-interval}")
  public void lock(final LockId lockId) {
    final int deviceId = lockId.value();
    try {
      lockApi.postLock(deviceId);
    } catch (final RestClientResponseException exception) {
      throw translateException(exception, deviceId);
    } catch (final RestClientException exception) {
      throw new LockTemporarilyUnavailableException(deviceId, exception);
    }
  }

  private RuntimeException translateException(
      final RestClientResponseException exception, final int deviceId) {
    return switch (HttpStatus.resolve(exception.getStatusCode().value())) {
      case NOT_FOUND -> new InvalidLockRequestException(deviceId, exception);
      case METHOD_NOT_ALLOWED, NOT_ACCEPTABLE, BAD_GATEWAY, SERVICE_UNAVAILABLE, GATEWAY_TIMEOUT ->
          new LockTemporarilyUnavailableException(deviceId, exception);
      case null, default -> new LockOperationFailedException(deviceId, exception);
    };
  }
}
