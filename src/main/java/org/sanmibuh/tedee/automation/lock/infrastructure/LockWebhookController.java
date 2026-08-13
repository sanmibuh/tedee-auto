package org.sanmibuh.tedee.automation.lock.infrastructure;

import org.sanmibuh.tedee.automation.lock.application.SyncLockStateUseCase;
import org.sanmibuh.tedee.automation.lock.domain.LockId;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Tedee webhook events.
 */
@RestController
@RequestMapping("/webhooks/lock")
public class LockWebhookController {

    private final SyncLockStateUseCase syncLockStateUseCase;

    public LockWebhookController(SyncLockStateUseCase syncLockStateUseCase) {
        this.syncLockStateUseCase = syncLockStateUseCase;
    }

    @PostMapping("/{lockId}/sync")
    public void syncLock(@PathVariable int lockId) {
        syncLockStateUseCase.execute(new LockId(lockId));
    }
}
