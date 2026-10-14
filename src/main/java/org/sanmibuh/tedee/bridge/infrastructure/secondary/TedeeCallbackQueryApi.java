package org.sanmibuh.tedee.bridge.infrastructure.secondary;

import com.tedee.bridge.client.api.CallbackApi;
import com.tedee.bridge.client.model.CallbackDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class TedeeCallbackQueryApi implements CallbackQueryApi {

  private final CallbackApi callbackApi;

  @Override
  public List<CallbackDetails> getCallbacks() {
    return callbackApi.getCallback();
  }
}
