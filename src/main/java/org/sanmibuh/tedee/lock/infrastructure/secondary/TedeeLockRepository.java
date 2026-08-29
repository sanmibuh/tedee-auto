package org.sanmibuh.tedee.lock.infrastructure.secondary;

import com.tedee.bridge.client.api.LockApi;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.domain.DomainEvent;
import org.sanmibuh.tedee.lock.domain.InvalidLockRequestException;
import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockId;
import org.sanmibuh.tedee.lock.domain.LockLocked;
import org.sanmibuh.tedee.lock.domain.LockOperationFailedException;
import org.sanmibuh.tedee.lock.domain.LockRepository;
import org.sanmibuh.tedee.lock.domain.LockStatus;
import org.sanmibuh.tedee.lock.domain.LockTemporarilyUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Repository
@RequiredArgsConstructor
public class TedeeLockRepository implements LockRepository {

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
    if (event instanceof LockLocked(int deviceId)) {
      lock(deviceId);
    }
  }

  private void lock(final int deviceId) {
    try {
      lockApi.postLock(deviceId);
    } catch (final RestClientResponseException exception) {
      throw toDomainException(exception, deviceId);
    } catch (final RestClientException exception) {
      throw new LockTemporarilyUnavailableException(deviceId, exception);
    }
  }

  private RuntimeException toDomainException(
      final RestClientResponseException exception, final int deviceId) {
    return switch (HttpStatus.resolve(exception.getStatusCode().value())) {
      case NOT_FOUND -> new InvalidLockRequestException(deviceId, exception);
      case METHOD_NOT_ALLOWED, NOT_ACCEPTABLE, BAD_GATEWAY, SERVICE_UNAVAILABLE, GATEWAY_TIMEOUT ->
          new LockTemporarilyUnavailableException(deviceId, exception);
      case null, default -> new LockOperationFailedException(deviceId, exception);
    };
  }
}
