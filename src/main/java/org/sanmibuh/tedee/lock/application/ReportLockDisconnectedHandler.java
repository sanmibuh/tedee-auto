package org.sanmibuh.tedee.lock.application;

import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockRepository;

public final class ReportLockDisconnectedHandler
    extends ReportLockHandler<ReportLockDisconnectedCommand> {

  public ReportLockDisconnectedHandler(final LockRepository repository) {
    super(repository);
  }

  @Override
  protected void report(final Lock lock) {
    lock.reportDisconnected();
  }
}
