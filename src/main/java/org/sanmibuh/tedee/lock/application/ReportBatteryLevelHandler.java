package org.sanmibuh.tedee.lock.application;

import org.sanmibuh.ddd.port.CommandHandler;
import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockRepository;

public final class ReportBatteryLevelHandler
    extends CommandHandler<ReportBatteryLevelCommand, Lock> {

  public ReportBatteryLevelHandler(final LockRepository repository) {}

  @Override
  protected Lock execute(final ReportBatteryLevelCommand command) {
    throw new UnsupportedOperationException();
  }
}
