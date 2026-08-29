package org.sanmibuh.tedee.lock.infrastructure.primary;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.cqrs.domain.CommandBus;
import org.sanmibuh.tedee.lock.application.CloseLockCommand;

@ExtendWith(MockitoExtension.class)
class LockSchedulerTest {

  @Mock CommandBus commandBus;

  @InjectMocks LockScheduler sut;

  @Test
  void should_dispatchCloseLockCommand_whenClosingLock() {
    sut.closeLock(12345);

    verify(commandBus).dispatch(new CloseLockCommand(12345));
  }
}
