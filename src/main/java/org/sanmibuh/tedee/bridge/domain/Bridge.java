package org.sanmibuh.tedee.bridge.domain;

import org.sanmibuh.ddd.domain.AggregateRoot;
import org.sanmibuh.tedee.bridge.domain.event.BridgeConnected;
import org.sanmibuh.tedee.bridge.domain.event.BridgeDisconnected;
import org.sanmibuh.tedee.bridge.domain.vo.BridgeId;

public final class Bridge extends AggregateRoot<BridgeId> {

  public Bridge() {
    super(BridgeId.SINGLETON);
  }

  public void reportConnected() {
    recordEvent(new BridgeConnected());
  }

  public void reportDisconnected() {
    recordEvent(new BridgeDisconnected());
  }
}
