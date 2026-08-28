package org.sanmibuh.tedee.lock.infrastructure;

import com.tedee.bridge.client.api.LockApi;
import lombok.RequiredArgsConstructor;
import org.sanmibuh.tedee.lock.domain.InvalidApiTokenException;
import org.sanmibuh.tedee.lock.domain.LockBleErrorException;
import org.sanmibuh.tedee.lock.domain.LockDisconnectedException;
import org.sanmibuh.tedee.lock.domain.LockId;
import org.sanmibuh.tedee.lock.domain.LockNotFoundException;
import org.sanmibuh.tedee.lock.domain.LockPort;
import org.sanmibuh.tedee.lock.domain.UnexpectedBridgeErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
@RequiredArgsConstructor
public class TedeeApiAdapter implements LockPort {

  private final LockApi lockApi;

  @Override
  public void lock(final LockId lockId) {
    try {
      lockApi.postLock(lockId.deviceId());
    } catch (final HttpClientErrorException exception) {
      throw toDomainException(exception, lockId);
    }
  }

  private RuntimeException toDomainException(
      final HttpClientErrorException exception, final LockId lockId) {
    return switch (HttpStatus.resolve(exception.getStatusCode().value())) {
      case UNAUTHORIZED -> new InvalidApiTokenException();
      case NOT_FOUND -> new LockNotFoundException(lockId.deviceId());
      case METHOD_NOT_ALLOWED -> new LockDisconnectedException(lockId.deviceId());
      case NOT_ACCEPTABLE -> new LockBleErrorException(lockId.deviceId());
      case null, default -> new UnexpectedBridgeErrorException(lockId.deviceId(), exception);
    };
  }
}
