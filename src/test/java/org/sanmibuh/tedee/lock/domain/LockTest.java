package org.sanmibuh.tedee.lock.domain;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;

class LockTest {

  @Test
  void should_haveLockedStatus_whenClosingUnlockedLock() {
    final var sut = new Lock(new LockId(1), LockStatus.UNLOCKED);

    sut.close();

    then(sut.status()).isEqualTo(LockStatus.LOCKED);
  }
}
