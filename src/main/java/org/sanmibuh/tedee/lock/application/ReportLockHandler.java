package org.sanmibuh.tedee.lock.application;

import org.sanmibuh.ddd.port.CommandHandler;
import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockRepository;
import org.sanmibuh.tedee.lock.domain.vo.LockId;

abstract class ReportLockHandler<C extends LockCommand> extends CommandHandler<C, Lock> {

  private final LockRepository repository;

  protected ReportLockHandler(final LockRepository repository) {
    this.repository = repository;
  }

  @Override
  protected final Lock execute(final C command) {
    final var lock = repository.get(new LockId(command.deviceId()));
    report(lock);
    repository.save(lock);

    return lock;
  }

  protected abstract void report(Lock lock);
}
