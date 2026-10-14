package org.sanmibuh.tedee.bridge.domain;

import java.net.URI;
import java.util.List;
import org.sanmibuh.ddd.domain.AggregateRoot;
import org.sanmibuh.tedee.bridge.domain.event.BridgeCallbackAdded;
import org.sanmibuh.tedee.bridge.domain.event.BridgeConnected;
import org.sanmibuh.tedee.bridge.domain.event.BridgeDisconnected;
import org.sanmibuh.tedee.bridge.domain.vo.BridgeId;

public final class Bridge extends AggregateRoot<BridgeId> {

  private final List<URI> callbacks;

  public Bridge() {
    this(List.of());
  }

  public Bridge(final List<URI> callbacks) {
    super(BridgeId.SINGLETON);
    this.callbacks = callbacks;
  }

  public void reportConnected() {
    recordEvent(new BridgeConnected());
  }

  public void reportDisconnected() {
    recordEvent(new BridgeDisconnected());
  }

  public void addCallback(final URI callbackUrl) {
    if (!callbacks.contains(callbackUrl)) {
      recordEvent(new BridgeCallbackAdded(callbackUrl));
    }
  }
}
