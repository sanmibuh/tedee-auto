package org.sanmibuh.tedee.lock.adapter.secondary;

import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.port.DomainEventHandler;
import org.sanmibuh.framework.notifier.Notifier;
import org.sanmibuh.tedee.ApplicationLocale;
import org.sanmibuh.tedee.lock.domain.event.LockSettingsChanged;
import org.springframework.context.MessageSource;

@RequiredArgsConstructor
public final class LockSettingsChangedNotificationHandler
    implements DomainEventHandler<LockSettingsChanged> {

  private final Notifier notifier;
  private final MessageSource messages;
  private final ApplicationLocale applicationLocale;

  @Override
  public void handle(final LockSettingsChanged event) {
    notifier.notify(
        messages.getMessage(
            "lock.settings_changed", new Object[] {event.deviceId()}, applicationLocale.current()));
  }
}
