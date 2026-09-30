package org.sanmibuh.tedee.lock.infrastructure.primary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sanmibuh.ddd.port.CommandBus;
import org.sanmibuh.tedee.lock.application.ReportLockStatusCommand;
import org.sanmibuh.tedee.lock.infrastructure.TedeeWebhookEndpoint;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
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

  @PostMapping(TedeeWebhookEndpoint.EVENTS_PATH)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void receive(@RequestBody final TedeeEvent event) {
    if (LOCK_STATUS_CHANGED.equals(event.event())) {
      final var data = jsonMapper.treeToValue(event.data(), LockStatusChangedData.class);
      commandBus.dispatch(
          new ReportLockStatusCommand(
              data.deviceId(), data.state(), data.jammed(), data.doorState()));
    } else {
      log.warn("Ignoring unknown Tedee Bridge event {}", event.event());
    }
  }

  record TedeeEvent(String event, JsonNode data) {}

  record LockStatusChangedData(int deviceId, int state, int jammed, int doorState) {}
}
