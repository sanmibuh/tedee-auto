package org.sanmibuh.tedee.lock.infrastructure.secondary;

import static org.sanmibuh.tedee.lock.infrastructure.TedeeInfrastructureConfiguration.*;

import com.tedee.bridge.client.api.CallbackApi;
import com.tedee.bridge.client.model.CallbackDetails;
import com.tedee.bridge.client.model.CallbackDetailsNoId;
import com.tedee.bridge.client.model.CallbackHeader;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.sanmibuh.tedee.ServerProperties;
import org.sanmibuh.tedee.lock.infrastructure.TedeeProperties;
import org.springframework.context.SmartLifecycle;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
final class TedeeCallbackRegistration implements SmartLifecycle {

  private final CallbackApi callbackApi;
  private final String callbackUrl;
  private final String callbackSecret;
  private @Nullable Long registeredId;

  TedeeCallbackRegistration(
      final CallbackApi callbackApi,
      final ServerProperties serverProperties,
      final TedeeProperties tedeeProperties) {
    this.callbackApi = callbackApi;
    callbackSecret = tedeeProperties.callbackSecret();
    callbackUrl =
        UriComponentsBuilder.fromUriString(serverProperties.publicUrl())
            .path(EVENTS_PATH)
            .toUriString();
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
            new CallbackDetailsNoId()
                .url(callbackUrl)
                .method(HttpMethod.POST.name())
                .addHeadersItem(
                    new CallbackHeader()
                        .headerName(CALLBACK_SECRET_HEADER + ": " + callbackSecret)));
    return Objects.requireNonNull(registered.getId());
  }

  private void delete(final long callbackId) {
    callbackApi.deleteCallback(Math.toIntExact(callbackId));
  }

  @Override
  public void stop() {
    if (registeredId != null) {
      try {
        delete(registeredId);
      } catch (final RestClientException exception) {
        log.warn("Could not unregister callback {} from the Tedee Bridge", callbackUrl, exception);
      } finally {
        registeredId = null;
      }
    }
  }

  @Override
  public boolean isRunning() {
    return registeredId != null;
  }
}
