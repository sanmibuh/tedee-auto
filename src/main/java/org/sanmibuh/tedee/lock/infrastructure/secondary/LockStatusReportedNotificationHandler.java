package org.sanmibuh.tedee.lock.infrastructure.secondary;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.port.DomainEventHandler;
import org.sanmibuh.framework.notifier.Notifier;
import org.sanmibuh.tedee.lock.domain.event.LockStatusReported;
import org.sanmibuh.tedee.lock.domain.vo.LockJamStatus;
import org.springframework.context.MessageSource;

@RequiredArgsConstructor
public final class LockStatusReportedNotificationHandler
    implements DomainEventHandler<LockStatusReported> {

  private final Notifier notifier;
  private final MessageSource messages;
  private final Locale locale;

  @Override
  public void handle(final LockStatusReported event) {
    notifier.notify(
        messages.getMessage(
            "lock.state." + event.state().name().toLowerCase(Locale.ROOT),
            new Object[] {event.deviceId()},
            locale));

    if (event.jamStatus() == LockJamStatus.JAMMED) {
      notifier.notify(messages.getMessage("lock.jammed", new Object[] {event.deviceId()}, locale));
    }
  }
}
