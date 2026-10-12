package org.sanmibuh.tedee.bridge.application;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.sanmibuh.tedee.bridge.domain.event.BridgeConnected;

class ReportBridgeConnectedHandlerTest {

  private final ReportBridgeConnectedHandler sut = new ReportBridgeConnectedHandler();

  @Test
  void should_recordBridgeConnected_whenReportingConnectedBridge() {
    final var events = sut.handle(new ReportBridgeConnectedCommand());

    then(events).containsExactly(new BridgeConnected());
  }
}
