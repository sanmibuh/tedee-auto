package org.sanmibuh.tedee.lock.application;

import lombok.RequiredArgsConstructor;
import org.sanmibuh.cqrs.domain.CommandHandler;
import org.sanmibuh.ddd.domain.AggregateNotFoundException;
import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockId;
import org.sanmibuh.tedee.lock.domain.LockPort;

@RequiredArgsConstructor
public class CloseLockHandler implements CommandHandler<CloseLockCommand> {

  private final LockPort lockPort;

  @Override
  public void handle(final CloseLockCommand command) {
    final var lockId = new LockId(command.deviceId());
    final var lock =
        lockPort
            .findById(lockId)
            .orElseThrow(() -> new AggregateNotFoundException(Lock.class, lockId));
    lock.lock();
    lockPort.save(lock);
  }
}
