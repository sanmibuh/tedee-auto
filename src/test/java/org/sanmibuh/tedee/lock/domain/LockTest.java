package org.sanmibuh.tedee.lock.domain;

import static org.assertj.core.api.BDDAssertions.then;

import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;

@ExtendWith(SoftAssertionsExtension.class)
class LockTest {

  @InjectSoftAssertions BDDSoftAssertions softly;

  @ParameterizedTest
  @EnumSource(
      value = LockStatus.class,
      mode = Mode.EXCLUDE,
      names = {"LOCKED"})
  void should_lockAndRecordEvent_whenLockingNonLockedLock(final LockStatus status) {
    final var sut = new Lock(new LockId(1), status);

    sut.lock();

    softly.then(sut.status()).isEqualTo(LockStatus.LOCKED);
    softly.then(sut.domainEvents()).containsExactly(new LockLocked(new LockId(1)));
  }

  @Test
  void should_notRecordEvent_whenLockingAlreadyLockedLock() {
    final var sut = new Lock(new LockId(1), LockStatus.LOCKED);

    sut.lock();

    then(sut.domainEvents()).isEmpty();
  }
}
