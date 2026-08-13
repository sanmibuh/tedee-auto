package org.sanmibuh.tedee.automation.lock.infrastructure;

import org.sanmibuh.tedee.automation.lock.application.SyncLockStateUseCase;
import org.sanmibuh.tedee.automation.lock.domain.LockId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Periodic lock state polling task.
 */
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
        if (lockId > 0) {
            syncLockStateUseCase.execute(new LockId(lockId));
        }
    }
}
