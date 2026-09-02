package org.sanmibuh.tedee.lock.application;

import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.port.CommandHandler;
import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockId;
import org.sanmibuh.tedee.lock.domain.LockRepository;

@RequiredArgsConstructor
public final class CloseLockHandler extends CommandHandler<CloseLockCommand, Lock> {

  private final LockRepository repository;

  @Override
  protected Lock execute(final CloseLockCommand command) {
    final var lock = repository.get(new LockId(command.deviceId()));
    lock.lock();
    repository.save(lock);

    return lock;
  }
}
