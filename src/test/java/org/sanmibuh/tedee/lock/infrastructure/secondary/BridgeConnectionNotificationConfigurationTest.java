package org.sanmibuh.tedee.lock.infrastructure.secondary;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.sanmibuh.framework.notifier.Notifier;
import org.sanmibuh.tedee.ApplicationLocale;
import org.sanmibuh.tedee.lock.infrastructure.BridgeConnectionNotifier;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.StaticMessageSource;

class BridgeConnectionNotificationConfigurationTest {

  private final ApplicationContextRunner runner =
      new ApplicationContextRunner()
          .withUserConfiguration(
              BridgeConnectionNotificationConfiguration.class, TestConfiguration.class);

  @Test
  void should_registerBridgeConnectionNotifier_whenConfigurationIsLoaded() {
    runner.run(context -> then(context).hasSingleBean(BridgeConnectionNotifier.class));
  }

  @Configuration
  static class TestConfiguration {

    @Bean
    Notifier notifier() {
      return message -> {};
    }

    @Bean
    StaticMessageSource messageSource() {
      return new StaticMessageSource();
    }

    @Bean
    ApplicationLocale applicationLocale() {
      return new ApplicationLocale(new WebProperties());
    }
  }
}
