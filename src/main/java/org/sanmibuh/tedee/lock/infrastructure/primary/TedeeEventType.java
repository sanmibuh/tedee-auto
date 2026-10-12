package org.sanmibuh.tedee.lock.infrastructure.primary;

import java.util.Optional;
import org.sanmibuh.ddd.port.Command;
import org.sanmibuh.tedee.lock.application.ReportLockStatusCommand;
import org.sanmibuh.tedee.lock.infrastructure.primary.TedeeEventController.LockStatusChangedData;
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
    final var eventData = jsonMapper.treeToValue(data, LockStatusChangedData.class);
    return new ReportLockStatusCommand(
        eventData.deviceId(), eventData.state(), eventData.jammed(), eventData.doorState());
  }
}
