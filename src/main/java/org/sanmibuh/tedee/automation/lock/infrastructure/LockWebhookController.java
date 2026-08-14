package org.sanmibuh.tedee.automation.lock.infrastructure;

import org.sanmibuh.tedee.automation.lock.application.SyncLockStateUseCase;
import org.sanmibuh.tedee.automation.lock.domain.LockId;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhooks/lock")
public class LockWebhookController {

	private final SyncLockStateUseCase syncLockStateUseCase;

	public LockWebhookController(SyncLockStateUseCase syncLockStateUseCase) {
		this.syncLockStateUseCase = syncLockStateUseCase;
	}

	@PostMapping("/{lockId}/sync")
	public void syncLock(@PathVariable int lockId) {
		this.syncLockStateUseCase.execute(new LockId(lockId));
	}
}
