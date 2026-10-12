package org.sanmibuh.tedee.bridge.domain.vo;

import org.sanmibuh.ddd.domain.AggregateRootId;

public final class BridgeId implements AggregateRootId<String> {

  public static final BridgeId SINGLETON = new BridgeId();

  private BridgeId() {}

  @Override
  public String value() {
    return "bridge";
  }
}
