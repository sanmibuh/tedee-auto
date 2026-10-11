package org.sanmibuh.framework.notifier;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.web.WebProperties;

class NotificationLocaleTest {

  @Test
  void should_returnWebLocale_whenCalled() {
    final var properties = new WebProperties();
    properties.setLocale(Locale.forLanguageTag("es"));
    final var sut = new NotificationLocale(properties);

    then(sut.current()).isEqualTo(Locale.forLanguageTag("es"));
  }
}
