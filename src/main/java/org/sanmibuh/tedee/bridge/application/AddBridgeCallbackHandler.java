package org.sanmibuh.tedee.bridge.application;

import org.sanmibuh.ddd.port.CommandHandler;
import org.sanmibuh.tedee.bridge.domain.Bridge;

public final class AddBridgeCallbackHandler
    extends CommandHandler<AddBridgeCallbackCommand, Bridge> {

  @Override
  protected Bridge execute(final AddBridgeCallbackCommand command) {
    return new Bridge();
  }
}
