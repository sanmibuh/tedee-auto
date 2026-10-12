package org.sanmibuh.tedee.bridge.application;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.sanmibuh.tedee.bridge.domain.event.BridgeDisconnected;

class ReportBridgeDisconnectedHandlerTest {

  private final ReportBridgeDisconnectedHandler sut = new ReportBridgeDisconnectedHandler();

  @Test
  void should_recordBridgeDisconnected_whenReportingDisconnectedBridge() {
    final var events = sut.handle(new ReportBridgeDisconnectedCommand());

    then(events).containsExactly(new BridgeDisconnected());
  }
}
