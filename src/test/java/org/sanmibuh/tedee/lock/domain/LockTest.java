package org.sanmibuh.tedee.lock.domain;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

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

  @Test
  void should_notRecordEvent_whenLockingAlreadyLockedLock() {
    final var sut = new Lock(new LockId(1), LockStatus.LOCKED);

    sut.lock();

    then(sut.domainEvents()).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(
      value = LockStatus.class,
      names = {"TRANSITIONING", "UNKNOWN"})
  void should_throwLockNotOperableException_whenLockingNonOperableLock(final LockStatus status) {
    final var sut = new Lock(new LockId(1), status);

    thenThrownBy(sut::lock).isInstanceOf(LockNotOperableException.class);
  }
}
