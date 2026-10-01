package org.sanmibuh.tedee.lock.application;

import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.port.CommandHandler;
import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockRepository;
import org.sanmibuh.tedee.lock.domain.vo.LockId;
import org.sanmibuh.tedee.lock.domain.vo.LockJamStatus;
import org.sanmibuh.tedee.lock.domain.vo.LockState;

@RequiredArgsConstructor
public final class ReportLockStatusHandler extends CommandHandler<ReportLockStatusCommand, Lock> {

  private final LockRepository repository;

  @Override
  protected Lock execute(final ReportLockStatusCommand command) {
    final var lock = repository.get(new LockId(command.deviceId()));
    lock.reportStatus(
        LockState.fromBridgeCode(command.state()), LockJamStatus.fromBridgeCode(command.jammed()));
    repository.save(lock);

    return lock;
  }
}
