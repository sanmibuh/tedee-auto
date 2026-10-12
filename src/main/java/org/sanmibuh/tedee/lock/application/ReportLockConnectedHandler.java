package org.sanmibuh.tedee.lock.application;

import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockRepository;

public final class ReportLockConnectedHandler
    extends ReportLockHandler<ReportLockConnectedCommand> {

  public ReportLockConnectedHandler(final LockRepository repository) {
    super(repository);
  }

  @Override
  protected void report(final Lock lock) {
    lock.reportConnected();
  }
}
