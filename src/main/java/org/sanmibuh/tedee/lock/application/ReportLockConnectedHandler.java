package org.sanmibuh.tedee.lock.application;

import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockRepository;
import org.sanmibuh.tedee.lock.domain.vo.LockId;

public final class ReportLockConnectedHandler
    extends ReportLockHandler<ReportLockConnectedCommand> {

  public ReportLockConnectedHandler(final LockRepository repository) {
    super(repository);
  }

  @Override
  protected LockId lockId(final ReportLockConnectedCommand command) {
    return new LockId(command.deviceId());
  }

  @Override
  protected void report(final Lock lock) {
    lock.reportConnected();
  }
}
