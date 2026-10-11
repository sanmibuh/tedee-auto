package org.sanmibuh.framework.notifier;

import java.util.Locale;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationConfiguration {

  @Bean
  Locale notificationLocale() {
    return Locale.ENGLISH;
  }
}
