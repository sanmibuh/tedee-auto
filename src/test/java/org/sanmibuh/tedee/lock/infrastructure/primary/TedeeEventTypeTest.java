package org.sanmibuh.tedee.lock.infrastructure.primary;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.sanmibuh.tedee.lock.application.ReportLockStatusCommand;
import tools.jackson.databind.json.JsonMapper;

class TedeeEventTypeTest {

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
}
