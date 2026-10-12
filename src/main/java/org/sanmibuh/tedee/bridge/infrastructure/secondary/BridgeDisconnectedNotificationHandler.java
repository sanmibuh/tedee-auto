package org.sanmibuh.tedee.bridge.infrastructure.secondary;

import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.port.DomainEventHandler;
import org.sanmibuh.framework.notifier.Notifier;
import org.sanmibuh.tedee.ApplicationLocale;
import org.sanmibuh.tedee.bridge.domain.event.BridgeDisconnected;
import org.springframework.context.MessageSource;

@RequiredArgsConstructor
public final class BridgeDisconnectedNotificationHandler
    implements DomainEventHandler<BridgeDisconnected> {

  private final Notifier notifier;
  private final MessageSource messages;
  private final ApplicationLocale applicationLocale;

  @Override
  public void handle(final BridgeDisconnected event) {
    notifier.notify(messages.getMessage("bridge.disconnected", null, applicationLocale.current()));
  }
}
