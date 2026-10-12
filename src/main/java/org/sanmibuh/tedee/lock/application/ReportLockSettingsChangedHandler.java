package org.sanmibuh.tedee.lock.application;

import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.port.CommandHandler;
import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockRepository;

@RequiredArgsConstructor
public final class ReportLockSettingsChangedHandler
    extends CommandHandler<ReportLockSettingsChangedCommand, Lock> {

  private final LockRepository repository;

  @Override
  protected Lock execute(final ReportLockSettingsChangedCommand command) {
    throw new UnsupportedOperationException();
  }
}
