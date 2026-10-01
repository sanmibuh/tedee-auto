package org.sanmibuh.tedee.lock.domain;

import org.sanmibuh.ddd.domain.AggregateRoot;
import org.sanmibuh.tedee.lock.domain.event.LockRequested;
import org.sanmibuh.tedee.lock.domain.event.LockStatusReported;
import org.sanmibuh.tedee.lock.domain.vo.LockId;
import org.sanmibuh.tedee.lock.domain.vo.LockJamStatus;
import org.sanmibuh.tedee.lock.domain.vo.LockState;
import org.sanmibuh.tedee.lock.domain.vo.LockStatus;

public final class Lock extends AggregateRoot<LockId> {

  private LockStatus status;

  public Lock(final LockId id, final LockStatus status) {
    super(id);
    this.status = status;
  }

  public void lock() {
    if (status == LockStatus.LOCKED) {
      return;
    }
    status = LockStatus.LOCKED;
    recordEvent(new LockRequested(id().value()));
  }

  public void reportStatus(final LockState state, final LockJamStatus jamStatus) {
    recordEvent(new LockStatusReported(id().value(), state, jamStatus));
  }
}
