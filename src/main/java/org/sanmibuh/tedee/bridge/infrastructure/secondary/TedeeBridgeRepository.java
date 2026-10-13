package org.sanmibuh.tedee.bridge.infrastructure.secondary;

import java.util.Optional;
import org.sanmibuh.tedee.bridge.domain.Bridge;
import org.sanmibuh.tedee.bridge.domain.BridgeRepository;
import org.sanmibuh.tedee.bridge.domain.vo.BridgeId;

public final class TedeeBridgeRepository implements BridgeRepository {

  @Override
  public Optional<Bridge> findById(final BridgeId bridgeId) {
    return Optional.empty();
  }

  @Override
  public void save(final Bridge bridge) {}
}
