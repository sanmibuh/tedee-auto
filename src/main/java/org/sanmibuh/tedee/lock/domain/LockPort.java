package org.sanmibuh.tedee.lock.domain;

public interface LockPort {

  Lock findById(LockId lockId);

  void save(Lock lock);

  void lock(LockId lockId);
}
