package org.sanmibuh.tedee.bridge.adapter.secondary;

import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.port.DomainEventHandler;
import org.sanmibuh.framework.notifier.Notifier;
import org.sanmibuh.tedee.ApplicationLocale;
import org.sanmibuh.tedee.bridge.domain.event.BridgeConnected;
import org.springframework.context.MessageSource;

@RequiredArgsConstructor
public final class BridgeConnectedNotificationHandler
    implements DomainEventHandler<BridgeConnected> {

  private final Notifier notifier;
  private final MessageSource messages;
  private final ApplicationLocale applicationLocale;

  @Override
  public void handle(final BridgeConnected event) {
    notifier.notify(messages.getMessage("bridge.connected", null, applicationLocale.current()));
  }
}
