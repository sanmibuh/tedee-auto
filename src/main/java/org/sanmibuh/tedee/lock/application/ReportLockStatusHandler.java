package org.sanmibuh.tedee.lock.application;

import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.port.CommandHandler;
import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockId;
import org.sanmibuh.tedee.lock.domain.LockRepository;

@RequiredArgsConstructor
public final class ReportLockStatusHandler extends CommandHandler<ReportLockStatusCommand, Lock> {

  private final LockRepository repository;

  @Override
  protected Lock execute(final ReportLockStatusCommand command) {
    final var lock = repository.get(new LockId(command.deviceId()));
    lock.reportStatus(command.state());
    repository.save(lock);

    return lock;
  }
}
