package org.sanmibuh.tedee.lock.infrastructure.primary;

import java.util.Optional;
import org.sanmibuh.ddd.port.Command;
import org.sanmibuh.tedee.bridge.application.ReportBridgeConnectedCommand;
import org.sanmibuh.tedee.bridge.application.ReportBridgeDisconnectedCommand;
import org.sanmibuh.tedee.lock.application.ReportBatteryLevelCommand;
import org.sanmibuh.tedee.lock.application.ReportLockConnectedCommand;
import org.sanmibuh.tedee.lock.application.ReportLockDisconnectedCommand;
import org.sanmibuh.tedee.lock.application.ReportLockSettingsChangedCommand;
import org.sanmibuh.tedee.lock.application.ReportLockStatusCommand;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

enum TedeeEventType {
  LOCK_STATUS_CHANGED("lock-status-changed") {
    @Override
    Command toCommand(final JsonNode data, final JsonMapper jsonMapper) {
      final var eventData = jsonMapper.treeToValue(data, LockStatusChangedData.class);
      return new ReportLockStatusCommand(
          eventData.deviceId(), eventData.state(), eventData.jammed(), eventData.doorState());
    }
  },
  BATTERY_LEVEL_CHANGED("device-battery-level-changed") {
    @Override
    Command toCommand(final JsonNode data, final JsonMapper jsonMapper) {
      final var eventData = jsonMapper.treeToValue(data, BatteryLevelChangedData.class);
      return new ReportBatteryLevelCommand(eventData.deviceId(), eventData.batteryLevel());
    }
  },
  DEVICE_CONNECTION_CHANGED("device-connection-changed") {
    @Override
    Command toCommand(final JsonNode data, final JsonMapper jsonMapper) {
      final var eventData = jsonMapper.treeToValue(data, DeviceConnectionChangedData.class);
      return eventData.isConnected() == 1
          ? new ReportLockConnectedCommand(eventData.deviceId())
          : new ReportLockDisconnectedCommand(eventData.deviceId());
    }
  },
  DEVICE_SETTINGS_CHANGED("device-settings-changed") {
    @Override
    Command toCommand(final JsonNode data, final JsonMapper jsonMapper) {
      final var eventData = jsonMapper.treeToValue(data, DeviceSettingsChangedData.class);
      return new ReportLockSettingsChangedCommand(eventData.deviceId());
    }
  },
  BACKEND_CONNECTION_CHANGED("backend-connection-changed") {
    @Override
    Command toCommand(final JsonNode data, final JsonMapper jsonMapper) {
      final var eventData = jsonMapper.treeToValue(data, BackendConnectionChangedData.class);
      return eventData.isConnected() == 1
          ? new ReportBridgeConnectedCommand()
          : new ReportBridgeDisconnectedCommand();
    }
  };

  private final String event;

  TedeeEventType(final String event) {
    this.event = event;
  }

  static Optional<TedeeEventType> from(final String event) {
    for (final var type : values()) {
      if (type.event.equals(event)) {
        return Optional.of(type);
      }
    }
    return Optional.empty();
  }

  abstract Command toCommand(JsonNode data, JsonMapper jsonMapper);

  record LockStatusChangedData(int deviceId, int state, int jammed, int doorState) {}

  record BatteryLevelChangedData(int deviceId, int batteryLevel) {}

  record DeviceConnectionChangedData(int deviceId, int isConnected) {}

  record DeviceSettingsChangedData(int deviceId) {}

  record BackendConnectionChangedData(int isConnected) {}
}
