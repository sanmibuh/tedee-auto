package org.sanmibuh.tedee.bridge.application;

import org.sanmibuh.ddd.port.CommandHandler;
import org.sanmibuh.tedee.bridge.domain.Bridge;

public final class ReportBridgeConnectedHandler
    extends CommandHandler<ReportBridgeConnectedCommand, Bridge> {

  @Override
  protected Bridge execute(final ReportBridgeConnectedCommand command) {
    final var bridge = new Bridge();
    bridge.reportConnected();

    return bridge;
  }
}
