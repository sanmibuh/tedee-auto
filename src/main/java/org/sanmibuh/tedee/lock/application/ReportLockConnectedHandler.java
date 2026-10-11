package org.sanmibuh.tedee.lock.application;

import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.port.CommandHandler;
import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockRepository;
import org.sanmibuh.tedee.lock.domain.vo.LockId;

@RequiredArgsConstructor
public final class ReportLockConnectedHandler
    extends CommandHandler<ReportLockConnectedCommand, Lock> {

  private final LockRepository repository;

  @Override
  protected Lock execute(final ReportLockConnectedCommand command) {
    final var lock = repository.get(new LockId(command.deviceId()));
    lock.reportConnected();
    repository.save(lock);

    return lock;
  }
}
