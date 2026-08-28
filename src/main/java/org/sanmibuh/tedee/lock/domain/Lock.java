package org.sanmibuh.tedee.lock.domain;

public class Lock {

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
    status = LockStatus.LOCKED;
  }
}
