package org.sanmibuh.tedee.lock.infrastructure.secondary;

import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.port.DomainEventHandler;
import org.sanmibuh.framework.notifier.Notifier;
import org.sanmibuh.tedee.ApplicationLocale;
import org.sanmibuh.tedee.lock.domain.event.BatteryLevelReported;
import org.springframework.context.MessageSource;

@RequiredArgsConstructor
public final class BatteryLevelReportedNotificationHandler
    implements DomainEventHandler<BatteryLevelReported> {

  private final Notifier notifier;
  private final MessageSource messages;
  private final ApplicationLocale applicationLocale;

  @Override
  public void handle(final BatteryLevelReported event) {
    notifier.notify(
        messages.getMessage(
            "battery.level.low",
            new Object[] {event.level(), event.deviceId()},
            applicationLocale.current()));
  }
}
