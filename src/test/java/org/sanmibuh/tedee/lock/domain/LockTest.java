package org.sanmibuh.tedee.lock.domain;

import static org.assertj.core.api.BDDAssertions.then;
import static org.sanmibuh.tedee.lock.LockSnapshotMother.snapshot;

import org.junit.jupiter.api.Test;
import org.sanmibuh.tedee.lock.domain.vo.LockState;

class LockTest {

  @Test
  void should_returnSnapshotWithName_whenCreatingLock() {
    final var expected = snapshot(LockState.OPEN);
    final var sut = new Lock(snapshot(LockState.OPEN));

    then(sut.snapshot()).isEqualTo(expected);
  }
}
