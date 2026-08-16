package org.sanmibuh.tedee.lock.domain;

public interface LockPort {

  void lock(LockId lockId);
}
