package org.sanmibuh.tedee.bridge.adapter.secondary.persistence;

import com.tedee.bridge.client.model.CallbackDetails;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sanmibuh.tedee.bridge.adapter.secondary.rest.CallbackQueryApi;
import org.sanmibuh.tedee.bridge.domain.Bridge;
import org.sanmibuh.tedee.bridge.domain.BridgeRepository;
import org.sanmibuh.tedee.bridge.domain.BridgeSnapshot;
import org.sanmibuh.tedee.bridge.domain.vo.BridgeId;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public final class TedeeBridgeRepository implements BridgeRepository {

  private final CallbackQueryApi callbackQueryApi;

  @Override
  public Optional<Bridge> findById(final BridgeId bridgeId) {
    final var callbacks =
        callbackQueryApi.getCallbacks().stream()
            .map(CallbackDetails::getUrl)
            .filter(Objects::nonNull)
            .map(URI::create)
            .toList();
    return Optional.of(new Bridge(new BridgeSnapshot(bridgeId.value(), callbacks)));
  }

  @Override
  public void save(final BridgeSnapshot snapshot) {
    // No-op
  }
}
