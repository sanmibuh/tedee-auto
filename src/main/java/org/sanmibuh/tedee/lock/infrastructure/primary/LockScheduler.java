package org.sanmibuh.tedee.lock.infrastructure.primary;

import lombok.RequiredArgsConstructor;
import org.sanmibuh.cqrs.domain.CommandBus;
import org.sanmibuh.tedee.lock.application.CloseLockCommand;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LockScheduler {

  private final CommandBus commandBus;

  public void closeLock(final int deviceId) {
    commandBus.dispatch(new CloseLockCommand(deviceId));
  }
}
