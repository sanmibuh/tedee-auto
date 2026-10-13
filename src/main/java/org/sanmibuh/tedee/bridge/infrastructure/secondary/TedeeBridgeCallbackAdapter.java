package org.sanmibuh.tedee.bridge.infrastructure.secondary;

import com.tedee.bridge.client.model.CallbackDetails;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.sanmibuh.tedee.bridge.projection.BridgeCallbackQueryPort;
import org.sanmibuh.tedee.lock.infrastructure.secondary.CallbackQueryApi;

@RequiredArgsConstructor
final class TedeeBridgeCallbackAdapter implements BridgeCallbackQueryPort {

  private final CallbackQueryApi callbackQueryApi;

  @Override
  public List<URI> getRegisteredCallbackUrls() {
    return callbackQueryApi.getCallbacks().stream()
        .map(CallbackDetails::getUrl)
        .filter(Objects::nonNull)
        .map(URI::create)
        .toList();
  }
}
