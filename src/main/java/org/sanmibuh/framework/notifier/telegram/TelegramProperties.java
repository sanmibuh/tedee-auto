package org.sanmibuh.framework.notifier.telegram;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "sanmibuh.notification.telegram")
public record TelegramProperties(
    @NotBlank String baseUrl, @NotBlank String botToken, @NotBlank String chatId) {}
