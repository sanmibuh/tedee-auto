package org.sanmibuh.framework.notifier.telegram;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.Nullable;
import org.sanmibuh.framework.resilience.RetryProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "sanmibuh.notification.telegram")
public record TelegramProperties(
    @NotBlank String baseUrl,
    @NotBlank String botToken,
    @NotBlank String chatId,
    @Nullable String adminChatId,
    @Valid @NotNull RetryProperties retry) {

  public TelegramProperties {
    adminChatId = adminChatId == null || adminChatId.isBlank() ? chatId : adminChatId;
  }
}
