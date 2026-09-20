package org.sanmibuh.tedee.lock.infrastructure.secondary;

import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.port.DomainEventHandler;
import org.sanmibuh.tedee.lock.domain.LockId;
import org.sanmibuh.tedee.lock.domain.LockLocked;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class LockLockedHandler implements DomainEventHandler<LockLocked> {

  private final LockGateway lockGateway;

  @Override
  public void handle(final LockLocked event) {
    lockGateway.lock(new LockId(event.deviceId()));
  }
}
