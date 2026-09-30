package org.sanmibuh.tedee.lock.infrastructure.secondary;

import com.tedee.bridge.client.api.CallbackApi;
import com.tedee.bridge.client.model.CallbackDetails;
import com.tedee.bridge.client.model.CallbackDetailsNoId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    if (keepSingleExistingCallback().isEmpty()) {
      register();
    }
  }

  private Optional<CallbackDetails> keepSingleExistingCallback() {
    final var ourCallbacks = callbacksMatching(callbackUrl());
    ourCallbacks.stream().skip(1).forEach(duplicate -> callbackApi.deleteCallback(idOf(duplicate)));
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

  private void register() {
    callbackApi.postSingleCallback(
        new CallbackDetailsNoId().url(callbackUrl()).method(HttpMethod.POST.name()));
  }

  private int idOf(final CallbackDetails callback) {
    return Math.toIntExact(Objects.requireNonNull(callback.getId()));
  }

  @Override
  public void stop() {}

  @Override
  public boolean isRunning() {
    return false;
  }
}
