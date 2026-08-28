package org.sanmibuh.tedee.lock.domain;

import java.util.Optional;

public interface LockPort {

  Optional<Lock> findById(LockId lockId);

  void save(Lock lock);

  void lock(LockId lockId);
}
