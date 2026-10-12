package org.sanmibuh.tedee.bridge.application;

import org.sanmibuh.ddd.port.CommandHandler;
import org.sanmibuh.tedee.bridge.domain.Bridge;

public final class ReportBridgeDisconnectedHandler
    extends CommandHandler<ReportBridgeDisconnectedCommand, Bridge> {

  @Override
  protected Bridge execute(final ReportBridgeDisconnectedCommand command) {
    final var bridge = new Bridge();
    bridge.reportDisconnected();

    return bridge;
  }
}
