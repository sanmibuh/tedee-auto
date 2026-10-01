package org.sanmibuh.framework.notifier.telegram;

import org.sanmibuh.ddd.domain.TransientIntegrationException;
import org.sanmibuh.framework.notifier.Notifier;
import org.springframework.resilience.annotation.Retryable;

interface TelegramGateway extends Notifier {

  @Override
  @Retryable(
      includes = TransientIntegrationException.class,
      maxRetriesString = "${sanmibuh.notification.telegram.retry.max-retries}",
      delayString = "${sanmibuh.notification.telegram.retry.delay}")
  void notify(String message);
}
