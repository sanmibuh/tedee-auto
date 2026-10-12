package org.sanmibuh.tedee.lock.infrastructure.primary;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.params.provider.Arguments.argumentSet;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.sanmibuh.ddd.port.Command;
import org.sanmibuh.tedee.bridge.application.ReportBridgeConnectedCommand;
import org.sanmibuh.tedee.bridge.application.ReportBridgeDisconnectedCommand;
import org.sanmibuh.tedee.lock.application.ReportBatteryLevelCommand;
import org.sanmibuh.tedee.lock.application.ReportLockConnectedCommand;
import org.sanmibuh.tedee.lock.application.ReportLockDisconnectedCommand;
import org.sanmibuh.tedee.lock.application.ReportLockSettingsChangedCommand;
import org.sanmibuh.tedee.lock.application.ReportLockStatusCommand;
import tools.jackson.databind.json.JsonMapper;

class TedeeEventTypeTest {

  static Stream<Arguments> callbackCommands() {
    return Stream.of(
        argumentSet(
            "battery level",
            "device-battery-level-changed",
            "{\"deviceId\": 42, \"batteryLevel\": 90}",
            new ReportBatteryLevelCommand(42, 90)),
        argumentSet(
            "connected lock",
            "device-connection-changed",
            "{\"deviceId\": 42, \"isConnected\": 1}",
            new ReportLockConnectedCommand(42)),
        argumentSet(
            "disconnected lock",
            "device-connection-changed",
            "{\"deviceId\": 42, \"isConnected\": 0}",
            new ReportLockDisconnectedCommand(42)),
        argumentSet(
            "settings change",
            "device-settings-changed",
            "{\"deviceId\": 42}",
            new ReportLockSettingsChangedCommand(42)),
        argumentSet(
            "connected bridge",
            "backend-connection-changed",
            "{\"isConnected\": 1}",
            new ReportBridgeConnectedCommand()),
        argumentSet(
            "disconnected bridge",
            "backend-connection-changed",
            "{\"isConnected\": 0}",
            new ReportBridgeDisconnectedCommand()));
  }

  @Test
  void should_returnEmpty_whenEventTypeIsUnknown() {
    then(TedeeEventType.from("unknown-event")).isEmpty();
  }

  @Test
  void should_returnLockStatusChanged_whenEventTypeIsKnown() {
    then(TedeeEventType.from("lock-status-changed")).contains(TedeeEventType.LOCK_STATUS_CHANGED);
  }

  @Test
  void should_createReportLockStatusCommand_whenConvertingLockStatusChangedData() {
    final var data =
        JsonMapper.builder()
            .build()
            .readTree("{\"deviceId\": 42, \"state\": 6, \"jammed\": 0, \"doorState\": 2}");

    final var command =
        TedeeEventType.LOCK_STATUS_CHANGED.toCommand(data, JsonMapper.builder().build());

    then(command).isEqualTo(new ReportLockStatusCommand(42, 6, 0, 2));
  }

  @ParameterizedTest
  @MethodSource("callbackCommands")
  void should_createCommand_whenConvertingKnownCallbackData(
      final String event, final String data, final Command expectedCommand) {
    final var jsonMapper = JsonMapper.builder().build();

    final var command =
        TedeeEventType.from(event).orElseThrow().toCommand(jsonMapper.readTree(data), jsonMapper);

    then(command).isEqualTo(expectedCommand);
  }
}
