package org.sanmibuh.tedee.bridge.infrastructure.secondary;

import java.util.Locale;
import org.sanmibuh.tedee.ApplicationLocale;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.support.ResourceBundleMessageSource;

final class BridgeNotificationTestSupport {

  private BridgeNotificationTestSupport() {}

  static ResourceBundleMessageSource messages() {
    final var messages = new ResourceBundleMessageSource();
    messages.setBasename("messages");
    messages.setDefaultEncoding("UTF-8");
    messages.setFallbackToSystemLocale(false);
    return messages;
  }

  static ApplicationLocale applicationLocale(final Locale locale) {
    final var webProperties = new WebProperties();
    webProperties.setLocale(locale);
    return new ApplicationLocale(webProperties);
  }
}
