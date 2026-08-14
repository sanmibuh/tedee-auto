package org.sanmibuh.tedee.automation.notification.infrastructure;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.sanmibuh.tedee.automation.notification.domain.NotificationPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class TelegramNotificationAdapter implements NotificationPort {

	private final RestClient restClient;

	@Value("${telegram.bot.token:}")
	private String botToken;

	@Value("${telegram.chat.id:}")
	private String chatId;

	public TelegramNotificationAdapter(RestClient.Builder builder) {
		this.restClient = builder.baseUrl("https://api.telegram.org").build();
	}

	@Override
	public void send(String message) {
		if (this.botToken.isBlank() || this.chatId.isBlank()) {
			return;
		}
		this.restClient.post().uri("/bot{token}/sendMessage", this.botToken)
				.body(new TelegramMessage(this.chatId, message)).retrieve().toBodilessEntity();
	}

	record TelegramMessage(@JsonProperty("chat_id") String chatId, String text) {
	}
}
