package org.sanmibuh.framework.notifier.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.sanmibuh.framework.notifier.Notifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
final class TelegramNotifier implements Notifier {

  private final TelegramProperties properties;
  private final RestClient client;

  TelegramNotifier(final RestClient.Builder builder, final TelegramProperties properties) {
    this.properties = properties;
    client = builder.baseUrl(properties.baseUrl() + "/bot" + properties.botToken()).build();
  }

  @Override
  public void notify(final String message) {
    client
        .post()
        .uri("/sendMessage")
        .body(new TelegramMessage(properties.chatId(), message))
        .retrieve()
        .toBodilessEntity();
  }

  private record TelegramMessage(@JsonProperty("chat_id") String chatId, String text) {}
}
