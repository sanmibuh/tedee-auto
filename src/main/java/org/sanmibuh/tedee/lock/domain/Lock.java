package org.sanmibuh.tedee.lock.domain;

import org.sanmibuh.ddd.domain.AggregateRoot;

public class Lock extends AggregateRoot {

  private final LockId id;
  private LockStatus status;

  public Lock(final LockId id, final LockStatus status) {
    this.id = id;
    this.status = status;
  }

  public LockId id() {
    return id;
  }

  public LockStatus status() {
    return status;
  }

  public void lock() {
    if (status == LockStatus.LOCKED) {
      return;
    }
    if (status != LockStatus.UNLOCKED) {
      throw new LockNotOperableException(id, status);
    }
    status = LockStatus.LOCKED;
    recordEvent(new LockLocked(id));
  }
}
