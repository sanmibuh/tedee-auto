package org.sanmibuh.tedee.lock.application;

import org.sanmibuh.ddd.port.Command;
import org.sanmibuh.ddd.port.CommandHandler;
import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockRepository;
import org.sanmibuh.tedee.lock.domain.vo.LockId;

abstract class ReportLockHandler<C extends Command> extends CommandHandler<C, Lock> {

  private final LockRepository repository;

  protected ReportLockHandler(final LockRepository repository) {
    this.repository = repository;
  }

  @Override
  protected final Lock execute(final C command) {
    final var lock = repository.get(lockId(command));
    report(lock);
    repository.save(lock);

    return lock;
  }

  protected abstract LockId lockId(C command);

  protected abstract void report(Lock lock);
}
