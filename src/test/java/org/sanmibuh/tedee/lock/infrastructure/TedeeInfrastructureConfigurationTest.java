package org.sanmibuh.tedee.lock.infrastructure;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.sanmibuh.framework.notifier.Notifier;
import org.sanmibuh.tedee.ApplicationLocale;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.StaticMessageSource;

class TedeeInfrastructureConfigurationTest {

  private final ApplicationContextRunner runner =
      new ApplicationContextRunner()
          .withUserConfiguration(TedeeInfrastructureConfiguration.class, TestConfiguration.class)
          .withPropertyValues(
              "sanmibuh.rest.tedee.base-url=http://bridge.local/v1.0",
              "sanmibuh.rest.tedee.api-key=secret-token",
              "sanmibuh.rest.tedee.callback-secret=callback-secret",
              "sanmibuh.rest.tedee.callback-registration-interval=30s",
              "sanmibuh.rest.tedee.retry.max-retries=0",
              "sanmibuh.rest.tedee.retry.initial-interval=1",
              "sanmibuh.rest.tedee.retry.multiplier=1",
              "sanmibuh.rest.tedee.retry.max-interval=1");

  @Test
  void should_registerTedeeProperties_whenConfigurationIsLoaded() {
    runner.run(context -> then(context).hasSingleBean(TedeeProperties.class));
  }

  @Test
  void should_registerApplicationLocale_whenConfigurationIsLoaded() {
    runner.run(context -> then(context).hasSingleBean(ApplicationLocale.class));
  }

  @Test
  void should_createApplicationLocale_whenRequested() {
    final var properties = new WebProperties();
    properties.setLocale(Locale.forLanguageTag("es"));

    final var applicationLocale =
        new TedeeInfrastructureConfiguration().applicationLocale(properties);

    then(applicationLocale.current()).isEqualTo(Locale.forLanguageTag("es"));
  }

  @Configuration
  static class TestConfiguration {

    @Bean
    Notifier notifier() {
      return message -> {};
    }

    @Bean
    MessageSource messageSource() {
      return new StaticMessageSource();
    }
  }
}
