package org.sanmibuh.framework.notifier.telegram;

import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

final class TelegramNotifier implements TelegramGateway {

  private final TelegramProperties properties;
  private final RestClient client;

  TelegramNotifier(final RestClient.Builder builder, final TelegramProperties properties) {
    this.properties = properties;
    client = builder.baseUrl(properties.baseUrl() + "/bot" + properties.botToken()).build();
  }

  @Override
  public void notify(final String message) {
    try {
      client
          .post()
          .uri("/sendMessage")
          .body(new TelegramMessageRequest(properties.chatId(), message))
          .retrieve()
          .toBodilessEntity();
    } catch (final RestClientResponseException exception) {
      if (exception.getStatusCode().is5xxServerError()
          || exception.getStatusCode().value() == 429) {
        throw new TelegramTemporarilyUnavailableException(sanitizedException());
      }
      throw new TelegramNotificationFailedException(sanitizedException());
    } catch (final RestClientException _) {
      throw new TelegramTemporarilyUnavailableException(sanitizedException());
    }
  }

  private RuntimeException sanitizedException() {
    return new IllegalStateException("Telegram request failed");
  }
}
