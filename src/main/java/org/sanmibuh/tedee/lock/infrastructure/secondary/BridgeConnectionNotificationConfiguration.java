package org.sanmibuh.tedee.lock.infrastructure.secondary;

import org.sanmibuh.framework.notifier.Notifier;
import org.sanmibuh.tedee.ApplicationLocale;
import org.sanmibuh.tedee.lock.infrastructure.BridgeConnectionNotifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BridgeConnectionNotificationConfiguration {

  @Bean
  BridgeConnectionNotifier bridgeConnectionNotifier(
      final Notifier notifier,
      final MessageSource messages,
      final ApplicationLocale applicationLocale) {
    return new BridgeConnectionNotificationHandler(notifier, messages, applicationLocale);
  }
}
