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
  LOCK_STATUS_CHANGED("lock-status-changed"),
  BATTERY_LEVEL_CHANGED("device-battery-level-changed"),
  DEVICE_CONNECTION_CHANGED("device-connection-changed"),
  DEVICE_SETTINGS_CHANGED("device-settings-changed"),
  BACKEND_CONNECTION_CHANGED("backend-connection-changed");

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

  Command toCommand(final JsonNode data, final JsonMapper jsonMapper) {
    return switch (this) {
      case LOCK_STATUS_CHANGED -> {
        final var eventData = jsonMapper.treeToValue(data, LockStatusChangedData.class);
        yield new ReportLockStatusCommand(
            eventData.deviceId(), eventData.state(), eventData.jammed(), eventData.doorState());
      }
      case BATTERY_LEVEL_CHANGED -> {
        final var eventData = jsonMapper.treeToValue(data, BatteryLevelChangedData.class);
        yield new ReportBatteryLevelCommand(eventData.deviceId(), eventData.batteryLevel());
      }
      case DEVICE_CONNECTION_CHANGED -> {
        final var eventData = jsonMapper.treeToValue(data, DeviceConnectionChangedData.class);
        yield eventData.isConnected()
            ? new ReportLockConnectedCommand(eventData.deviceId())
            : new ReportLockDisconnectedCommand(eventData.deviceId());
      }
      case DEVICE_SETTINGS_CHANGED -> {
        final var eventData = jsonMapper.treeToValue(data, DeviceSettingsChangedData.class);
        yield new ReportLockSettingsChangedCommand(eventData.deviceId());
      }
      case BACKEND_CONNECTION_CHANGED -> {
        final var eventData = jsonMapper.treeToValue(data, BackendConnectionChangedData.class);
        yield eventData.isConnected()
            ? new ReportBridgeConnectedCommand()
            : new ReportBridgeDisconnectedCommand();
      }
    };
  }

  record LockStatusChangedData(int deviceId, int state, int jammed, int doorState) {}

  record BatteryLevelChangedData(int deviceId, int batteryLevel) {}

  record DeviceConnectionChangedData(int deviceId, boolean isConnected) {}

  record DeviceSettingsChangedData(int deviceId) {}

  record BackendConnectionChangedData(boolean isConnected) {}
}
