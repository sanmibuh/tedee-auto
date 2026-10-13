package org.sanmibuh.tedee.bridge.infrastructure.secondary;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sanmibuh.tedee.bridge.projection.BridgeCallbackQueryPort;
import org.sanmibuh.tedee.lock.infrastructure.secondary.CallbackQueryApi;

@RequiredArgsConstructor
final class TedeeBridgeCallbackProjection implements BridgeCallbackQueryPort {

  private final CallbackQueryApi callbackQueryApi;

  @Override
  public List<URI> getRegisteredCallbackUrls() {
    return callbackQueryApi.getCallbacks().stream()
        .map(callback -> URI.create(callback.getUrl()))
        .toList();
  }
}
