package org.sanmibuh.tedee.lock.infrastructure.primary;

import lombok.RequiredArgsConstructor;
import org.sanmibuh.cqrs.domain.CommandBus;

@RequiredArgsConstructor
public class LockScheduler {

  private final CommandBus commandBus;

  public void closeLock(final int deviceId) {
    throw new UnsupportedOperationException();
  }
}
