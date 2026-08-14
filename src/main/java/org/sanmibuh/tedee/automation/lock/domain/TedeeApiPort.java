package org.sanmibuh.tedee.automation.lock.domain;

public interface TedeeApiPort {

	Lock fetchLock(LockId id);

	void lock(LockId id);

	void unlock(LockId id);
}
