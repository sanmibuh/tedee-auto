package org.sanmibuh.tedee.lock.application;

import lombok.RequiredArgsConstructor;
import org.sanmibuh.cqrs.domain.CommandHandler;
import org.sanmibuh.tedee.lock.domain.LockId;
import org.sanmibuh.tedee.lock.domain.LockPort;

@RequiredArgsConstructor
public class CloseLockHandler implements CommandHandler<CloseLockCommand> {

  private final LockPort lockPort;

  @Override
  public void handle(final CloseLockCommand command) {
    final var lock = lockPort.findById(new LockId(command.deviceId()));
    lock.lock();
    lockPort.save(lock);
  }
}
