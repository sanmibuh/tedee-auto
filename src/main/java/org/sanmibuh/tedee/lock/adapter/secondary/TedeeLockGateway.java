package org.sanmibuh.tedee.lock.adapter.secondary;

import com.tedee.bridge.client.api.LockApi;
import lombok.RequiredArgsConstructor;
import org.sanmibuh.tedee.lock.domain.exception.InvalidLockRequestException;
import org.sanmibuh.tedee.lock.domain.exception.LockOperationFailedException;
import org.sanmibuh.tedee.lock.domain.exception.LockTemporarilyUnavailableException;
import org.sanmibuh.tedee.lock.domain.vo.LockId;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@RequiredArgsConstructor
final class TedeeLockGateway implements LockGateway {

  private final LockApi lockApi;

  @Override
  public void lock(final LockId lockId) {
    final var deviceId = lockId.value();
    try {
      lockApi.postLock(deviceId);
    } catch (final RestClientResponseException exception) {
      throw translateException(exception, deviceId);
    } catch (final RestClientException exception) {
      throw new LockTemporarilyUnavailableException(deviceId, exception);
    }
  }

  @Override
  public LockView get(final LockId lockId) {
    final var deviceId = lockId.value();
    try {
      final var details = lockApi.getLockById(deviceId);
      return new LockView(
          deviceId,
          StringUtils.hasText(details.getName()) ? details.getName() : String.valueOf(deviceId));
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
      case METHOD_NOT_ALLOWED,
          NOT_ACCEPTABLE,
          CONFLICT,
          BAD_GATEWAY,
          SERVICE_UNAVAILABLE,
          GATEWAY_TIMEOUT ->
          new LockTemporarilyUnavailableException(deviceId, exception);
      case null, default -> new LockOperationFailedException(deviceId, exception);
    };
  }
}
