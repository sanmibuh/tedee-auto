package org.sanmibuh.tedee.lock.infrastructure;

import com.tedee.bridge.client.api.LockApi;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.domain.DomainEvent;
import org.sanmibuh.tedee.lock.domain.InvalidLockRequestException;
import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockId;
import org.sanmibuh.tedee.lock.domain.LockLocked;
import org.sanmibuh.tedee.lock.domain.LockOperationFailedException;
import org.sanmibuh.tedee.lock.domain.LockPort;
import org.sanmibuh.tedee.lock.domain.LockTemporarilyUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
@RequiredArgsConstructor
public class TedeeApiAdapter implements LockPort {

  private final LockApi lockApi;

  @Override
  public Optional<Lock> findById(final LockId lockId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void save(final Lock lock) {
    lock.domainEvents().forEach(event -> apply(event, lock.id()));
  }

  private void apply(final DomainEvent event, final LockId lockId) {
    if (event instanceof LockLocked) {
      lock(lockId);
    }
  }

  private void lock(final LockId lockId) {
    try {
      lockApi.postLock(lockId.deviceId());
    } catch (final RestClientResponseException exception) {
      throw toDomainException(exception, lockId);
    } catch (final RestClientException exception) {
      throw new LockTemporarilyUnavailableException(lockId.deviceId(), exception);
    }
  }

  private RuntimeException toDomainException(
      final RestClientResponseException exception, final LockId lockId) {
    return switch (HttpStatus.resolve(exception.getStatusCode().value())) {
      case NOT_FOUND -> new InvalidLockRequestException(lockId.deviceId(), exception);
      case METHOD_NOT_ALLOWED, NOT_ACCEPTABLE ->
          new LockTemporarilyUnavailableException(lockId.deviceId(), exception);
      case null, default -> new LockOperationFailedException(lockId.deviceId(), exception);
    };
  }
}
