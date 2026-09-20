package org.sanmibuh.tedee.lock.infrastructure.secondary;

import java.util.Optional;
import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockId;
import org.sanmibuh.tedee.lock.domain.LockRepository;
import org.sanmibuh.tedee.lock.domain.LockStatus;
import org.springframework.stereotype.Repository;

@Repository
public final class TedeeLockRepository implements LockRepository {

  @Override
  public Optional<Lock> findById(final LockId lockId) {
    return Optional.of(new Lock(lockId, LockStatus.UNLOCKED));
  }

  @Override
  public void save(final Lock lock) {
    // No persistence store yet: locking is a reaction to the LockLocked domain event,
    // handled by LockLockedHandler. Persisting aggregate state belongs here once a store exists.
  }
}
