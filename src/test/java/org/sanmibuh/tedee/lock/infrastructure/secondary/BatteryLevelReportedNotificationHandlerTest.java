package org.sanmibuh.tedee.lock.infrastructure.secondary;

import static org.mockito.BDDMockito.then;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.framework.notifier.Notifier;
import org.sanmibuh.tedee.ApplicationLocale;
import org.sanmibuh.tedee.lock.domain.event.BatteryLevelReported;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.support.ResourceBundleMessageSource;

@ExtendWith(MockitoExtension.class)
class BatteryLevelReportedNotificationHandlerTest {

  @Mock Notifier notifier;

  @Test
  void should_notifyLocalizedLowBatteryLevel_whenHandlingReportedLevel() {
    final var sut =
        new BatteryLevelReportedNotificationHandler(
            notifier, messages(), applicationLocale(Locale.ENGLISH));

    sut.handle(new BatteryLevelReported(42, 40));

    then(notifier).should().notify("Battery low: 40% (lock 42)");
  }

  private ResourceBundleMessageSource messages() {
    final var messages = new ResourceBundleMessageSource();
    messages.setBasename("messages");
    messages.setDefaultEncoding("UTF-8");
    messages.setFallbackToSystemLocale(false);
    return messages;
  }

  private ApplicationLocale applicationLocale(final Locale locale) {
    final var webProperties = new WebProperties();
    webProperties.setLocale(locale);
    return new ApplicationLocale(webProperties);
  }
}
