package org.sanmibuh.tedee.lock.infrastructure.secondary;

import com.tedee.bridge.client.api.CallbackApi;
import com.tedee.bridge.client.model.CallbackDetails;
import com.tedee.bridge.client.model.CallbackDetailsNoId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.sanmibuh.tedee.ServerProperties;
import org.springframework.context.SmartLifecycle;
import org.springframework.http.HttpMethod;

final class TedeeCallbackRegistration implements SmartLifecycle {

  private static final String CALLBACK_PATH = "/tedee/events";

  private final CallbackApi callbackApi;
  private final String callbackUrl;
  private long registeredId;

  TedeeCallbackRegistration(
      final CallbackApi callbackApi, final ServerProperties serverProperties) {
    this.callbackApi = callbackApi;
    callbackUrl = serverProperties.publicUrl() + CALLBACK_PATH;
  }

  @Override
  public void start() {
    registeredId =
        keepSingleExistingCallback()
            .map(callback -> Objects.requireNonNull(callback.getId()))
            .orElseGet(this::register);
  }

  private Optional<CallbackDetails> keepSingleExistingCallback() {
    final var ourCallbacks = callbacksMatching();
    ourCallbacks.stream()
        .skip(1)
        .forEach(duplicate -> delete(Objects.requireNonNull(duplicate.getId())));
    return ourCallbacks.stream().findFirst();
  }

  private List<CallbackDetails> callbacksMatching() {
    return callbackApi.getCallback().stream()
        .filter(callback -> callbackUrl.equals(callback.getUrl()))
        .toList();
  }

  private long register() {
    final var registered =
        callbackApi.postSingleCallback(
            new CallbackDetailsNoId().url(callbackUrl).method(HttpMethod.POST.name()));
    return Objects.requireNonNull(registered.getId());
  }

  private void delete(final long callbackId) {
    callbackApi.deleteCallback(Math.toIntExact(callbackId));
  }

  @Override
  public void stop() {
    delete(registeredId);
  }

  @Override
  public boolean isRunning() {
    return false;
  }
}
