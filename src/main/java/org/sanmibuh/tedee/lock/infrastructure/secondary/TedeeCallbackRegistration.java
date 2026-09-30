package org.sanmibuh.tedee.lock.infrastructure.secondary;

import com.tedee.bridge.client.api.CallbackApi;
import com.tedee.bridge.client.model.CallbackDetails;
import com.tedee.bridge.client.model.CallbackDetailsNoId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.sanmibuh.tedee.ServerProperties;
import org.springframework.context.SmartLifecycle;
import org.springframework.http.HttpMethod;

@RequiredArgsConstructor
final class TedeeCallbackRegistration implements SmartLifecycle {

  private static final String CALLBACK_PATH = "/tedee/events";

  private final CallbackApi callbackApi;
  private final ServerProperties serverProperties;
  private int registeredId;

  @Override
  public void start() {
    registeredId =
        keepSingleExistingCallback()
            .map(callback -> toId(callback.getId()))
            .orElseGet(this::register);
  }

  private Optional<CallbackDetails> keepSingleExistingCallback() {
    final var ourCallbacks = callbacksMatching(callbackUrl());
    ourCallbacks.stream()
        .skip(1)
        .forEach(duplicate -> callbackApi.deleteCallback(toId(duplicate.getId())));
    return ourCallbacks.stream().findFirst();
  }

  private String callbackUrl() {
    return serverProperties.publicUrl() + CALLBACK_PATH;
  }

  private List<CallbackDetails> callbacksMatching(final String callbackUrl) {
    return callbackApi.getCallback().stream()
        .filter(callback -> callbackUrl.equals(callback.getUrl()))
        .toList();
  }

  private int register() {
    final var registered =
        callbackApi.postSingleCallback(
            new CallbackDetailsNoId().url(callbackUrl()).method(HttpMethod.POST.name()));
    return toId(registered.getId());
  }

  private static int toId(final @Nullable Long id) {
    return Math.toIntExact(Objects.requireNonNull(id));
  }

  @Override
  public void stop() {
    callbackApi.deleteCallback(registeredId);
  }

  @Override
  public boolean isRunning() {
    return false;
  }
}
