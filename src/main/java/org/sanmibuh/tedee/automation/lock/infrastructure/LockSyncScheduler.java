package org.sanmibuh.tedee.automation.lock.infrastructure;

import org.sanmibuh.tedee.automation.lock.application.SyncLockStateUseCase;
import org.sanmibuh.tedee.automation.lock.domain.LockId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LockSyncScheduler {

	private final SyncLockStateUseCase syncLockStateUseCase;

	@Value("${tedee.lock.id:0}")
	private int lockId;

	public LockSyncScheduler(SyncLockStateUseCase syncLockStateUseCase) {
		this.syncLockStateUseCase = syncLockStateUseCase;
	}

	@Scheduled(fixedDelayString = "${tedee.sync.interval-ms:60000}")
	public void sync() {
		if (this.lockId > 0) {
			this.syncLockStateUseCase.execute(new LockId(this.lockId));
		}
	}
}
