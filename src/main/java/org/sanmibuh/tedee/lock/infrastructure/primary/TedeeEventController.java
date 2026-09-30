package org.sanmibuh.tedee.lock.infrastructure.primary;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.sanmibuh.ddd.port.CommandBus;
import org.sanmibuh.tedee.lock.application.ReportLockStatusCommand;
import org.sanmibuh.tedee.lock.infrastructure.TedeeProperties;
import org.sanmibuh.tedee.lock.infrastructure.TedeeWebhookEndpoint;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@RestController
@RequiredArgsConstructor
@ImportRuntimeHints(TedeeEventRuntimeHints.class)
final class TedeeEventController {

  private static final String LOCK_STATUS_CHANGED = "lock-status-changed";

  private final CommandBus commandBus;
  private final JsonMapper jsonMapper;
  private final TedeeProperties tedeeProperties;

  @PostMapping(TedeeWebhookEndpoint.EVENTS_PATH)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void receive(
      @RequestHeader(value = TedeeWebhookEndpoint.CALLBACK_SECRET_HEADER, required = false)
          final @Nullable String callbackSecret,
      @RequestBody final TedeeEvent event) {
    if (!isCallbackSecretValid(callbackSecret)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
    if (LOCK_STATUS_CHANGED.equals(event.event())) {
      final var data = jsonMapper.treeToValue(event.data(), LockStatusChangedData.class);
      commandBus.dispatch(
          new ReportLockStatusCommand(
              data.deviceId(), data.state(), data.jammed(), data.doorState()));
    } else {
      log.warn("Ignoring unknown Tedee Bridge event {}", event.event());
    }
  }

  private boolean isCallbackSecretValid(final @Nullable String callbackSecret) {
    return callbackSecret != null
        && MessageDigest.isEqual(
            tedeeProperties.callbackSecret().getBytes(StandardCharsets.UTF_8),
            callbackSecret.getBytes(StandardCharsets.UTF_8));
  }

  record TedeeEvent(String event, JsonNode data) {}

  record LockStatusChangedData(int deviceId, int state, int jammed, int doorState) {}
}
