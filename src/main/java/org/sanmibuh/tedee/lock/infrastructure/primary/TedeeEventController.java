package org.sanmibuh.tedee.lock.infrastructure.primary;

import static org.sanmibuh.tedee.lock.infrastructure.TedeeInfrastructureConfiguration.*;
import static org.springframework.http.HttpStatus.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sanmibuh.ddd.domain.DomainException;
import org.sanmibuh.ddd.port.CommandBus;
import org.sanmibuh.tedee.lock.application.ReportBatteryLevelCommand;
import org.sanmibuh.tedee.lock.application.ReportLockConnectedCommand;
import org.sanmibuh.tedee.lock.application.ReportLockDisconnectedCommand;
import org.sanmibuh.tedee.lock.application.ReportLockSettingsChangedCommand;
import org.sanmibuh.tedee.lock.application.ReportLockStatusCommand;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
  private static final String BATTERY_LEVEL_CHANGED = "device-battery-level-changed";
  private static final String DEVICE_CONNECTION_CHANGED = "device-connection-changed";
  private static final String DEVICE_SETTINGS_CHANGED = "device-settings-changed";

  private final CommandBus commandBus;
  private final JsonMapper jsonMapper;

  @PostMapping(EVENTS_PATH)
  @ResponseStatus(NO_CONTENT)
  void receive(@RequestBody final TedeeEvent event) {
    switch (event.event()) {
      case LOCK_STATUS_CHANGED -> {
        final var data = jsonMapper.treeToValue(event.data(), LockStatusChangedData.class);
        commandBus.dispatch(
            new ReportLockStatusCommand(
                data.deviceId(), data.state(), data.jammed(), data.doorState()));
      }
      case BATTERY_LEVEL_CHANGED -> {
        final var data = jsonMapper.treeToValue(event.data(), BatteryLevelChangedData.class);
        commandBus.dispatch(new ReportBatteryLevelCommand(data.deviceId(), data.batteryLevel()));
      }
      case DEVICE_CONNECTION_CHANGED -> {
        final var data = jsonMapper.treeToValue(event.data(), DeviceConnectionChangedData.class);
        commandBus.dispatch(
            data.isConnected()
                ? new ReportLockConnectedCommand(data.deviceId())
                : new ReportLockDisconnectedCommand(data.deviceId()));
      }
      case DEVICE_SETTINGS_CHANGED -> {
        final var data = jsonMapper.treeToValue(event.data(), DeviceSettingsChangedData.class);
        commandBus.dispatch(new ReportLockSettingsChangedCommand(data.deviceId()));
      }
      default -> log.warn("Ignoring unknown Tedee Bridge event {}", event.event());
    }
  }

  @ExceptionHandler(DomainException.class)
  @ResponseStatus(NO_CONTENT)
  void handleDomainException(final DomainException exception) {
    log.warn("Ignoring rejected Tedee Bridge event: {}", exception.getMessage());
  }

  record TedeeEvent(String event, JsonNode data) {}

  record LockStatusChangedData(int deviceId, int state, int jammed, int doorState) {}

  record BatteryLevelChangedData(int deviceId, int batteryLevel) {}

  record DeviceConnectionChangedData(int deviceId, boolean isConnected) {}

  record DeviceSettingsChangedData(int deviceId) {}
}
