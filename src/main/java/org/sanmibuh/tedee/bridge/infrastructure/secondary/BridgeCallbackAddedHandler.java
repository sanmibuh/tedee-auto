package org.sanmibuh.tedee.bridge.infrastructure.secondary;

import org.sanmibuh.ddd.port.DomainEventHandler;
import org.sanmibuh.tedee.bridge.domain.event.BridgeCallbackAdded;

public final class BridgeCallbackAddedHandler implements DomainEventHandler<BridgeCallbackAdded> {

  @Override
  public void handle(final BridgeCallbackAdded event) {}
}
