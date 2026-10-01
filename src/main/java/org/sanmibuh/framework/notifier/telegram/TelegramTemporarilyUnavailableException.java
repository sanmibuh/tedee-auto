package org.sanmibuh.framework.notifier.telegram;

import org.sanmibuh.ddd.domain.TransientIntegrationException;

public final class TelegramTemporarilyUnavailableException extends TransientIntegrationException {

  public TelegramTemporarilyUnavailableException(final Throwable cause) {
    super("Telegram is temporarily unavailable", cause);
  }
}
