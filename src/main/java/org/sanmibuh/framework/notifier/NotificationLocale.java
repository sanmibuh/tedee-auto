package org.sanmibuh.framework.notifier;

import java.util.Locale;
import org.springframework.boot.autoconfigure.web.WebProperties;

public final class NotificationLocale {

  public NotificationLocale(final WebProperties properties) {}

  public Locale current() {
    return Locale.ENGLISH;
  }
}
