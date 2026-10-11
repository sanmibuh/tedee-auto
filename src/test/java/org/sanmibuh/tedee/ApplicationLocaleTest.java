package org.sanmibuh.tedee;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.web.WebProperties;

class ApplicationLocaleTest {

  @Test
  void should_returnWebLocale_whenCalled() {
    final var properties = new WebProperties();
    properties.setLocale(Locale.forLanguageTag("es"));
    final var sut = new ApplicationLocale(properties);

    then(sut.current()).isEqualTo(Locale.forLanguageTag("es"));
  }

  @Test
  void should_returnEnglish_whenWebLocaleIsNotConfigured() {
    final var sut = new ApplicationLocale(new WebProperties());

    then(sut.current()).isEqualTo(Locale.ENGLISH);
  }
}
