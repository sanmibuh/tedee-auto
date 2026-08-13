package org.sanmibuh.tedee.automation.lock.domain;

import java.util.Optional;

/**
 * Output port: persistence for locks.
 */
public interface LockRepository {

    void save(Lock lock);

    Optional<Lock> findById(LockId id);
}
