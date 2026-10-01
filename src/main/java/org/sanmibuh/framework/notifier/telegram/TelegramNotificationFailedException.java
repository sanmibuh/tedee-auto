package org.sanmibuh.framework.notifier.telegram;

import org.sanmibuh.ddd.domain.IntegrationException;

public final class TelegramNotificationFailedException extends IntegrationException {

  public TelegramNotificationFailedException(final Throwable cause) {
    super("Telegram notification failed", cause);
  }
}
