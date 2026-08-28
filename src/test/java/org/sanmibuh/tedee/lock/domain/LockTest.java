package org.sanmibuh.tedee.lock.domain;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;

class LockTest {

  @Test
  void should_haveLockedStatus_whenLockingUnlockedLock() {
    final var sut = new Lock(new LockId(1), LockStatus.UNLOCKED);

    sut.lock();

    then(sut.status()).isEqualTo(LockStatus.LOCKED);
  }

  @Test
  void should_recordLockLockedEvent_whenLockingUnlockedLock() {
    final var sut = new Lock(new LockId(1), LockStatus.UNLOCKED);

    sut.lock();

    then(sut.domainEvents()).containsExactly(new LockLocked(new LockId(1)));
  }
}
