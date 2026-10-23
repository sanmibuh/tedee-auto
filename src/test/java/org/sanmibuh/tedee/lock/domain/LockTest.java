package org.sanmibuh.tedee.lock.domain;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.sanmibuh.tedee.lock.domain.vo.LockId;
import org.sanmibuh.tedee.lock.domain.vo.LockStatus;

class LockTest {

  @Test
  void should_returnSnapshot_whenCreatingLock() {
    final var snapshot = new LockSnapshot(1, LockStatus.UNLOCKED, List.of());
    final var sut = Lock.create(new LockId(1), LockStatus.UNLOCKED);

    then(sut.snapshot()).isEqualTo(snapshot);
  }
}
