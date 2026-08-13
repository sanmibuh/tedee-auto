package org.sanmibuh.tedee.automation.lock.domain;

/**
 * Output port: Tedee API client for lock operations.
 */
public interface TedeeApiPort {

    Lock fetchLock(LockId id);

    void lock(LockId id);

    void unlock(LockId id);
}
