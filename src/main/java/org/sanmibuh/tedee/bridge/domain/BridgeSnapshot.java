package org.sanmibuh.tedee.bridge.domain;

import java.net.URI;
import java.util.List;
import org.sanmibuh.ddd.domain.Snapshot;

public record BridgeSnapshot(List<URI> callbacks) implements Snapshot {

  public BridgeSnapshot {
    callbacks = List.copyOf(callbacks);
  }
}
