package org.sanmibuh.tedee.lock.domain;

import static org.assertj.core.api.BDDAssertions.then;
import static org.sanmibuh.tedee.lock.LockSnapshotMother.snapshot;

import org.junit.jupiter.api.Test;
import org.sanmibuh.tedee.lock.domain.vo.LockStatus;

class LockTest {

  @Test
  void should_returnSnapshotWithName_whenCreatingLock() {
    final var expected = snapshot(LockStatus.UNLOCKED);
    final var sut = new Lock(snapshot(LockStatus.UNLOCKED));

    then(sut.snapshot()).isEqualTo(expected);
  }
}
