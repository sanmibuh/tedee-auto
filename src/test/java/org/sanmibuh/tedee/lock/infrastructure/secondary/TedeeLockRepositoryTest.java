package org.sanmibuh.tedee.lock.infrastructure.secondary;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenCode;

import org.junit.jupiter.api.Test;
import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockId;
import org.sanmibuh.tedee.lock.domain.LockStatus;

class TedeeLockRepositoryTest {

  private static final int DEVICE_ID = 42;

  private final TedeeLockRepository sut = new TedeeLockRepository();

  @Test
  void should_returnLock_whenFindingById() {
    then(sut.findById(new LockId(DEVICE_ID))).isPresent();
  }

  @Test
  void should_doNothing_whenSavingLockedLock() {
    final var lock = new Lock(new LockId(DEVICE_ID), LockStatus.UNLOCKED);

    thenCode(() -> sut.save(lock)).doesNotThrowAnyException();
  }
}
