package org.sanmibuh.tedee.lock.domain;

import org.sanmibuh.ddd.domain.AggregateRoot;

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
