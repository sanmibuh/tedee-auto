package org.sanmibuh.tedee.lock.infrastructure.secondary;

import com.tedee.bridge.client.api.LockApi;
import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.port.DomainEventHandler;
import org.sanmibuh.tedee.lock.domain.LockLocked;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class LockLockedHandler implements DomainEventHandler<LockLocked> {

  private final LockApi lockApi;

  @Override
  public void handle(final LockLocked event) {
    throw new UnsupportedOperationException();
  }
}
