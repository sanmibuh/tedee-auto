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
            "battery.level." + levelName(event.level()),
            new Object[] {event.level(), event.deviceId()},
            applicationLocale.current()));
  }

  private String levelName(final int level) {
    if (level <= 10) {
      return "depleted";
    }
    if (level <= 20) {
      return "critical";
    }
    if (level <= 40) {
      return "low";
    }
    if (level <= 60) {
      return "medium";
    }
    if (level <= 80) {
      return "good";
    }
    return "high";
  }
}
