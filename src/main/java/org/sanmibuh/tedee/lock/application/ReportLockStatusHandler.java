package org.sanmibuh.tedee.lock.application;

import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.port.CommandHandler;
import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockRepository;

@RequiredArgsConstructor
public final class ReportLockStatusHandler extends CommandHandler<ReportLockStatusCommand, Lock> {

  private final LockRepository repository;

  @Override
  protected Lock execute(final ReportLockStatusCommand command) {
    throw new UnsupportedOperationException();
  }
}
