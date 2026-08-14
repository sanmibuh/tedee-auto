package org.sanmibuh.tedee.automation.lock.application;

import org.sanmibuh.tedee.automation.lock.domain.Lock;
import org.sanmibuh.tedee.automation.lock.domain.LockId;
import org.sanmibuh.tedee.automation.lock.domain.LockRepository;
import org.sanmibuh.tedee.automation.lock.domain.TedeeApiPort;
import org.springframework.stereotype.Service;

@Service
public class SyncLockStateUseCase {

	private final TedeeApiPort tedeeApiPort;
	private final LockRepository lockRepository;

	public SyncLockStateUseCase(TedeeApiPort tedeeApiPort, LockRepository lockRepository) {
		this.tedeeApiPort = tedeeApiPort;
		this.lockRepository = lockRepository;
	}

	public Lock execute(LockId lockId) {
		final Lock lock = this.tedeeApiPort.fetchLock(lockId);
		this.lockRepository.save(lock);
		return lock;
	}
}
