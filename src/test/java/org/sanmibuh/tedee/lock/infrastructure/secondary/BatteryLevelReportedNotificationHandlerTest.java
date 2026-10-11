package org.sanmibuh.tedee.lock.infrastructure.secondary;

import static org.mockito.BDDMockito.then;

import java.util.Locale;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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

  static Stream<Arguments> batteryLevels() {
    return Stream.of(
        Arguments.of(10, "Battery depleted: 10% (lock 42)"),
        Arguments.of(20, "Battery critical: 20% (lock 42)"),
        Arguments.of(40, "Battery low: 40% (lock 42)"),
        Arguments.of(60, "Battery medium: 60% (lock 42)"),
        Arguments.of(80, "Battery good: 80% (lock 42)"),
        Arguments.of(81, "Battery high: 81% (lock 42)"));
  }

  @ParameterizedTest
  @MethodSource("batteryLevels")
  void should_notifyLocalizedBatteryLevel_whenHandlingReportedLevel(
      final int level, final String message) {
    final var sut =
        new BatteryLevelReportedNotificationHandler(
            notifier, messages(), applicationLocale(Locale.ENGLISH));

    sut.handle(new BatteryLevelReported(42, level));

    then(notifier).should().notify(message);
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
