package org.sanmibuh.tedee.bridge.domain;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.sanmibuh.ddd.domain.AggregateRoot;
import org.sanmibuh.tedee.bridge.domain.event.BridgeCallbackAdded;
import org.sanmibuh.tedee.bridge.domain.event.BridgeConnected;
import org.sanmibuh.tedee.bridge.domain.event.BridgeDisconnected;
import org.sanmibuh.tedee.bridge.domain.vo.BridgeId;

public final class Bridge extends AggregateRoot<BridgeId, String, BridgeSnapshot> {

  private final List<URI> callbacks = new ArrayList<>();

  private Bridge() {
    super(BridgeId.SINGLETON);
  }

  public Bridge(final BridgeSnapshot snapshot) {
    this();
    callbacks.addAll(snapshot.callbacks());
  }

  public static Bridge create() {
    return new Bridge(new BridgeSnapshot(BridgeId.SINGLETON.value(), List.of()));
  }

  @Override
  public BridgeSnapshot snapshot() {
    return new BridgeSnapshot(id().value(), callbacks);
  }

  public void reportConnected() {
    recordEvent(new BridgeConnected());
  }

  public void reportDisconnected() {
    recordEvent(new BridgeDisconnected());
  }

  public void addCallback(final URI callbackUrl) {
    if (!callbacks.contains(callbackUrl)) {
      callbacks.add(callbackUrl);
      recordEvent(new BridgeCallbackAdded(callbackUrl));
    }
  }
}
