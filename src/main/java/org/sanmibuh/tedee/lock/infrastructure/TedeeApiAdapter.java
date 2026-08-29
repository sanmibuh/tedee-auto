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
import org.sanmibuh.tedee.lock.domain.LockStatus;
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
    return Optional.of(new Lock(lockId, LockStatus.UNLOCKED));
  }

  @Override
  public void save(final Lock lock) {
    lock.domainEvents().forEach(this::apply);
  }

  private void apply(final DomainEvent event) {
    if (event instanceof LockLocked(LockId lockId)) {
      lock(lockId);
    }
  }

  private void lock(final LockId lockId) {
    try {
      lockApi.postLock(lockId.value());
    } catch (final RestClientResponseException exception) {
      throw toDomainException(exception, lockId);
    } catch (final RestClientException exception) {
      throw new LockTemporarilyUnavailableException(lockId.value(), exception);
    }
  }

  private RuntimeException toDomainException(
      final RestClientResponseException exception, final LockId lockId) {
    return switch (HttpStatus.resolve(exception.getStatusCode().value())) {
      case NOT_FOUND -> new InvalidLockRequestException(lockId.value(), exception);
      case METHOD_NOT_ALLOWED, NOT_ACCEPTABLE, BAD_GATEWAY, SERVICE_UNAVAILABLE, GATEWAY_TIMEOUT ->
          new LockTemporarilyUnavailableException(lockId.value(), exception);
      case null, default -> new LockOperationFailedException(lockId.value(), exception);
    };
  }
}
