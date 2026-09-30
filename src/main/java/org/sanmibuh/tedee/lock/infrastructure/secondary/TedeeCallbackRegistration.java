package org.sanmibuh.tedee.lock.infrastructure.secondary;

import com.tedee.bridge.client.api.CallbackApi;
import com.tedee.bridge.client.model.CallbackDetails;
import com.tedee.bridge.client.model.CallbackDetailsNoId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.sanmibuh.tedee.ServerProperties;
import org.springframework.context.SmartLifecycle;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
final class TedeeCallbackRegistration implements SmartLifecycle {

  private static final String CALLBACK_PATH = "/tedee/events";

  private final CallbackApi callbackApi;
  private final String callbackUrl;
  private @Nullable Long registeredId;

  TedeeCallbackRegistration(
      final CallbackApi callbackApi, final ServerProperties serverProperties) {
    this.callbackApi = callbackApi;
    callbackUrl = serverProperties.publicUrl() + CALLBACK_PATH;
  }

  @Override
  public void start() {
    try {
      registeredId =
          keepSingleExistingCallback()
              .map(callback -> Objects.requireNonNull(callback.getId()))
              .orElseGet(this::register);
    } catch (final RestClientException exception) {
      log.warn("Could not register callback {} on the Tedee Bridge", callbackUrl, exception);
    }
  }

  private Optional<CallbackDetails> keepSingleExistingCallback() {
    final var ourCallbacks = ourCallbacks();
    ourCallbacks.stream()
        .skip(1)
        .forEach(duplicate -> delete(Objects.requireNonNull(duplicate.getId())));
    return ourCallbacks.stream().findFirst();
  }

  private List<CallbackDetails> ourCallbacks() {
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
    if (registeredId != null) {
      delete(registeredId);
      registeredId = null;
    }
  }

  @Override
  public boolean isRunning() {
    return registeredId != null;
  }
}
