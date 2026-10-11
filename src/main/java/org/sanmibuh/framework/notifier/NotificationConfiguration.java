package org.sanmibuh.framework.notifier;

import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationConfiguration {

  @Bean
  Locale notificationLocale(@Value("${sanmibuh.notification.locale:en}") final String locale) {
    return Locale.forLanguageTag(locale);
  }
}
