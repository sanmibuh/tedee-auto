package org.sanmibuh.framework.notifier.telegram;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.sanmibuh.framework.resilience.RetryProperties;
import org.springframework.web.client.RestClient;

class TelegramNotificationConfigurationTest {

  private final TelegramNotificationConfiguration sut = new TelegramNotificationConfiguration();

  @Test
  void should_createTelegramNotifier_whenConfigured() {
    final var notifier =
        sut.telegramNotifier(
            RestClient.builder(),
            new TelegramProperties(
                "http://telegram.local",
                "bot-token",
                "chat-id",
                new RetryProperties(2, 500, 2.0, 5000)));

    then(notifier).isInstanceOf(TelegramNotifier.class);
  }
}
