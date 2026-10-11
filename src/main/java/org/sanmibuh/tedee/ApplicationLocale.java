package org.sanmibuh.tedee;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class ApplicationLocale {

  private final WebProperties properties;

  public Locale current() {
    final var locale = properties.getLocale();
    return locale == null ? Locale.ENGLISH : locale;
  }
}
