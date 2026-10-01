package org.sanmibuh.framework.notifier.telegram;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.web.client.RestClient;

@AutoConfiguration
@EnableResilientMethods
@EnableConfigurationProperties(TelegramProperties.class)
@ImportRuntimeHints(TelegramRuntimeHints.class)
public class TelegramNotificationConfiguration {

  @Bean
  TelegramGateway telegramNotifier(
      final RestClient.Builder builder, final TelegramProperties properties) {
    return new TelegramNotifier(builder, properties);
  }
}
