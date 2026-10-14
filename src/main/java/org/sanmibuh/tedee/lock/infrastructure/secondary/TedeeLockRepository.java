package org.sanmibuh.tedee.lock.infrastructure.secondary;

import java.util.Optional;
import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockRepository;
import org.sanmibuh.tedee.lock.domain.LockSnapshot;
import org.sanmibuh.tedee.lock.domain.vo.LockId;
import org.sanmibuh.tedee.lock.domain.vo.LockStatus;
import org.springframework.stereotype.Repository;

@Repository
public final class TedeeLockRepository implements LockRepository {

  @Override
  public Optional<Lock> findById(final LockId lockId) {
    return Optional.of(new Lock(new LockSnapshot(lockId.value(), LockStatus.UNLOCKED)));
  }

  @Override
  public void save(final LockSnapshot snapshot) {
    // No persistence store yet: locking is a reaction to the LockRequested domain event,
    // handled by LockRequestedHandler. Persisting aggregate state belongs here once a store exists.
  }
}
