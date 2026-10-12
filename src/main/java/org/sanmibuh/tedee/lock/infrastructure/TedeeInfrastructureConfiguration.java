package org.sanmibuh.tedee.lock.infrastructure;

import org.sanmibuh.framework.notifier.Notifier;
import org.sanmibuh.tedee.ApplicationLocale;
import org.sanmibuh.tedee.lock.infrastructure.primary.BridgeConnectionNotifier;
import org.sanmibuh.tedee.lock.infrastructure.secondary.BridgeConnectionNotificationHandler;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({TedeeProperties.class, WebProperties.class})
public class TedeeInfrastructureConfiguration {

  public static final String EVENTS_PATH = "/tedee/events";
  public static final String CALLBACK_SECRET_HEADER = "X-Tedee-Callback-Secret";

  @Bean
  ApplicationLocale applicationLocale(final WebProperties webProperties) {
    return new ApplicationLocale(webProperties);
  }

  @Bean
  BridgeConnectionNotifier bridgeConnectionNotifier(
      final Notifier notifier,
      final MessageSource messages,
      final ApplicationLocale applicationLocale) {
    return new BridgeConnectionNotificationHandler(notifier, messages, applicationLocale);
  }
}
