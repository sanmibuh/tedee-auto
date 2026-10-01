package org.sanmibuh.framework.notifier.telegram;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
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
                new TelegramProperties.Retry(2, 500)));

    then(notifier).isInstanceOf(TelegramNotifier.class);
  }
}
