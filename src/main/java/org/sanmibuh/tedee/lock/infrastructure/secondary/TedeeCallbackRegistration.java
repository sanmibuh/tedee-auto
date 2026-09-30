package org.sanmibuh.tedee.lock.infrastructure.secondary;

import com.tedee.bridge.client.api.CallbackApi;
import com.tedee.bridge.client.model.CallbackDetailsNoId;
import java.util.Objects;
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
    final var callbackUrl = serverProperties.publicUrl() + CALLBACK_PATH;
    final var ourCallbacks =
        callbackApi.getCallback().stream()
            .filter(callback -> callbackUrl.equals(callback.getUrl()))
            .toList();
    if (ourCallbacks.isEmpty()) {
      callbackApi.postSingleCallback(
          new CallbackDetailsNoId().url(callbackUrl).method(HttpMethod.POST.name()));
    }
    ourCallbacks.stream()
        .skip(1)
        .forEach(
            duplicate ->
                callbackApi.deleteCallback(
                    Math.toIntExact(Objects.requireNonNull(duplicate.getId()))));
  }

  @Override
  public void stop() {}

  @Override
  public boolean isRunning() {
    return false;
  }
}
