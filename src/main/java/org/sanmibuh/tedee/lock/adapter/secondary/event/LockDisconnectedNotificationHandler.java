package org.sanmibuh.tedee.lock.adapter.secondary.event;

import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.port.DomainEventHandler;
import org.sanmibuh.framework.notifier.Notifier;
import org.sanmibuh.tedee.ApplicationLocale;
import org.sanmibuh.tedee.lock.domain.event.LockDisconnected;
import org.springframework.context.MessageSource;

@RequiredArgsConstructor
public final class LockDisconnectedNotificationHandler
    implements DomainEventHandler<LockDisconnected> {

  private final Notifier notifier;
  private final MessageSource messages;
  private final ApplicationLocale applicationLocale;

  @Override
  public void handle(final LockDisconnected event) {
    notifier.notify(
        messages.getMessage(
            "lock.disconnected", new Object[] {event.name()}, applicationLocale.current()));
  }
}
