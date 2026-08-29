package org.sanmibuh.tedee.lock.domain;

import org.sanmibuh.ddd.domain.AggregateRoot;

public class Lock extends AggregateRoot {

  private final LockId id;
  private LockStatus status;

  public Lock(final LockId id, final LockStatus status) {
    this.id = id;
    this.status = status;
  }

  public void lock() {
    if (status == LockStatus.LOCKED) {
      return;
    }
    status = LockStatus.LOCKED;
    recordEvent(new LockLocked(id));
  }
}
