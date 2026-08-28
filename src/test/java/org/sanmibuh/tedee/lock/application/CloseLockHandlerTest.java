package org.sanmibuh.tedee.lock.application;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockId;
import org.sanmibuh.tedee.lock.domain.LockPort;
import org.sanmibuh.tedee.lock.domain.LockStatus;

@ExtendWith(MockitoExtension.class)
class CloseLockHandlerTest {

  @Mock LockPort lockPort;

  @InjectMocks CloseLockHandler sut;

  @Test
  void should_lockTheLock_whenHandlingCloseLockCommand() {
    final var lockId = new LockId(1);
    final var lock = new Lock(lockId, LockStatus.UNLOCKED);
    given(lockPort.findById(lockId)).willReturn(lock);

    sut.handle(new CloseLockCommand(1));

    then(lock.status()).isEqualTo(LockStatus.LOCKED);
    verify(lockPort).save(lock);
  }
}
