package org.sanmibuh.tedee.lock.adapter.secondary.event;

import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.port.DomainEventHandler;
import org.sanmibuh.framework.notifier.Notifier;
import org.sanmibuh.tedee.ApplicationLocale;
import org.sanmibuh.tedee.lock.domain.event.LockConnected;
import org.springframework.context.MessageSource;

@RequiredArgsConstructor
public final class LockConnectedNotificationHandler implements DomainEventHandler<LockConnected> {

  private final Notifier notifier;
  private final MessageSource messages;
  private final ApplicationLocale applicationLocale;

  @Override
  public void handle(final LockConnected event) {
    notifier.notify(
        messages.getMessage(
            "lock.connected", new Object[] {event.name()}, applicationLocale.current()));
  }
}
