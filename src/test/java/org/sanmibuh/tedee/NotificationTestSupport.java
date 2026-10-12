package org.sanmibuh.tedee;

import java.util.Locale;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.support.ResourceBundleMessageSource;

public final class NotificationTestSupport {

  public static final Locale SPANISH = Locale.forLanguageTag("es");

  private NotificationTestSupport() {}

  public static ResourceBundleMessageSource messages() {
    final var messages = new ResourceBundleMessageSource();
    messages.setBasename("messages");
    messages.setDefaultEncoding("UTF-8");
    messages.setFallbackToSystemLocale(false);
    return messages;
  }

  public static ApplicationLocale applicationLocale(final Locale locale) {
    final var webProperties = new WebProperties();
    webProperties.setLocale(locale);
    return new ApplicationLocale(webProperties);
  }
}
