package org.sanmibuh.tedee.lock.infrastructure.secondary;

import com.tedee.bridge.client.api.CallbackApi;
import com.tedee.bridge.client.model.CallbackDetailsNoId;
import com.tedee.bridge.client.model.CallbackIDsingle;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class TedeeCallbackCommandApi implements CallbackCommandApi {

  private final CallbackApi callbackApi;

  @Override
  public CallbackIDsingle createCallback(final CallbackDetailsNoId callback) {
    return callbackApi.postSingleCallback(callback);
  }

  @Override
  public void deleteCallback(final int callbackId) {
    callbackApi.deleteCallback(callbackId);
  }
}
