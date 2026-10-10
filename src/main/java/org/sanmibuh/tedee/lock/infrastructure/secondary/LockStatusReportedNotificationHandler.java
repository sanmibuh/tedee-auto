package org.sanmibuh.tedee.lock.infrastructure.secondary;

import java.util.Locale;
import org.sanmibuh.ddd.port.DomainEventHandler;
import org.sanmibuh.framework.notifier.Notifier;
import org.sanmibuh.tedee.lock.domain.event.LockStatusReported;
import org.springframework.context.MessageSource;

public final class LockStatusReportedNotificationHandler
    implements DomainEventHandler<LockStatusReported> {

  public LockStatusReportedNotificationHandler(
      final Notifier notifier, final MessageSource messages, final Locale locale) {}

  @Override
  public void handle(final LockStatusReported event) {}
}
