package org.sanmibuh.framework.notifier.telegram;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "sanmibuh.notification.telegram")
public record TelegramProperties(
    @NotBlank String baseUrl,
    @NotBlank String botToken,
    @NotBlank String chatId,
    @Valid @NotNull Retry retry) {

  public record Retry(@Positive int maxRetries, @Positive long delay) {}
}
