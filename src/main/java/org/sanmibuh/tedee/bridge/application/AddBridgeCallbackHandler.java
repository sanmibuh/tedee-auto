package org.sanmibuh.tedee.bridge.application;

import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.port.CommandHandler;
import org.sanmibuh.tedee.bridge.domain.Bridge;
import org.sanmibuh.tedee.bridge.domain.BridgeRepository;
import org.sanmibuh.tedee.bridge.domain.vo.BridgeId;

@RequiredArgsConstructor
public final class AddBridgeCallbackHandler
    extends CommandHandler<AddBridgeCallbackCommand, Bridge> {

  private final BridgeRepository repository;

  @Override
  protected Bridge execute(final AddBridgeCallbackCommand command) {
    final var bridge = repository.get(BridgeId.SINGLETON);
    bridge.addCallback(command.callbackUrl());
    repository.save(bridge);

    return bridge;
  }
}
