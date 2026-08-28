package org.sanmibuh.tedee.lock.application;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.tedee.lock.domain.LockId;
import org.sanmibuh.tedee.lock.domain.LockPort;

@ExtendWith(MockitoExtension.class)
class CloseLockHandlerTest {

  @Mock LockPort lockPort;

  @InjectMocks CloseLockHandler sut;

  @Test
  void should_delegateToLockPort_whenHandlingCloseLockCommand() {
    final var command = new CloseLockCommand(1);

    sut.handle(command);

    verify(lockPort).lock(new LockId(1));
  }
}
