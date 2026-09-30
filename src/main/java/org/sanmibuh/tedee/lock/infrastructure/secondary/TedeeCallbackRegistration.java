package org.sanmibuh.tedee.lock.infrastructure.secondary;

import com.tedee.bridge.client.api.CallbackApi;
import com.tedee.bridge.client.model.CallbackDetailsNoId;
import lombok.RequiredArgsConstructor;
import org.sanmibuh.tedee.ServerProperties;
import org.springframework.context.SmartLifecycle;
import org.springframework.http.HttpMethod;

@RequiredArgsConstructor
final class TedeeCallbackRegistration implements SmartLifecycle {

  private static final String CALLBACK_PATH = "/tedee/events";

  private final CallbackApi callbackApi;
  private final ServerProperties serverProperties;

  @Override
  public void start() {
    callbackApi.postSingleCallback(
        new CallbackDetailsNoId()
            .url(serverProperties.publicUrl() + CALLBACK_PATH)
            .method(HttpMethod.POST.name()));
  }

  @Override
  public void stop() {}

  @Override
  public boolean isRunning() {
    return false;
  }
}
