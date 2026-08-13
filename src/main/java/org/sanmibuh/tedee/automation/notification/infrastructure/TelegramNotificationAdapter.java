package org.sanmibuh.tedee.automation.notification.infrastructure;

import org.sanmibuh.tedee.automation.notification.domain.NotificationPort;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Telegram Bot API adapter for sending notifications.
 */
@Component
public class TelegramNotificationAdapter implements NotificationPort {

    private final RestClient restClient;

    @Value("${telegram.bot.token:}")
    private String botToken;

    @Value("${telegram.chat.id:}")
    private String chatId;

    public TelegramNotificationAdapter(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://api.telegram.org")
                .build();
    }

    @Override
    public void send(String message) {
        if (botToken.isBlank() || chatId.isBlank()) {
            return;
        }
        restClient.post()
                .uri("/bot{token}/sendMessage", botToken)
                .body(new TelegramMessage(chatId, message))
                .retrieve()
                .toBodilessEntity();
    }

    record TelegramMessage(@JsonProperty("chat_id") String chatId, String text) {}
}
