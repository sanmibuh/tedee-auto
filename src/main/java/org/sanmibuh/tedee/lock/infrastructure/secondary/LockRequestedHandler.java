package org.sanmibuh.tedee.lock.infrastructure.secondary;

import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.port.DomainEventHandler;
import org.sanmibuh.tedee.lock.domain.event.LockRequested;
import org.sanmibuh.tedee.lock.domain.vo.LockId;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class LockRequestedHandler implements DomainEventHandler<LockRequested> {

  private final LockGateway lockGateway;

  @Override
  public void handle(final LockRequested event) {
    lockGateway.lock(new LockId(event.deviceId()));
  }
}
