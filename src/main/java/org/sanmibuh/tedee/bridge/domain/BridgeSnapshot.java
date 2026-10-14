package org.sanmibuh.tedee.bridge.domain;

import java.net.URI;
import java.util.List;
import org.sanmibuh.ddd.domain.EntitySnapshot;

public record BridgeSnapshot(String id, List<URI> callbacks) implements EntitySnapshot<String> {

  public BridgeSnapshot {
    callbacks = List.copyOf(callbacks);
  }
}
