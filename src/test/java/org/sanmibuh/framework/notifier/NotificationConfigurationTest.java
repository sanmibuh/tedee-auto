package org.sanmibuh.framework.notifier;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class NotificationConfigurationTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner().withUserConfiguration(NotificationConfiguration.class);

  @Test
  void should_createConfiguredLocale_whenNotificationLocaleIsSpanish() {
    contextRunner
        .withPropertyValues("sanmibuh.notification.locale=es")
        .run(context -> then(context.getBean(Locale.class)).isEqualTo(Locale.forLanguageTag("es")));
  }
}
