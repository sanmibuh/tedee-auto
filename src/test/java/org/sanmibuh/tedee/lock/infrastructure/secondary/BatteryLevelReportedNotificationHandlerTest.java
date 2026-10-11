package org.sanmibuh.tedee.lock.infrastructure.secondary;

import static org.mockito.BDDMockito.then;
import static org.sanmibuh.tedee.lock.infrastructure.secondary.NotificationTestSupport.applicationLocale;
import static org.sanmibuh.tedee.lock.infrastructure.secondary.NotificationTestSupport.messages;

import java.util.Locale;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.framework.notifier.Notifier;
import org.sanmibuh.tedee.lock.domain.event.BatteryLevelReported;
import org.sanmibuh.tedee.lock.domain.event.BatteryLevelReported.BatteryStatus;
import org.sanmibuh.tedee.lock.domain.vo.BatteryLevelCategory;

@ExtendWith(MockitoExtension.class)
class BatteryLevelReportedNotificationHandlerTest {

  @Mock Notifier notifier;

  static Stream<Arguments> batteryLevels() {
    return Stream.of(
        Arguments.of(10, BatteryLevelCategory.DEPLETED, "Battery depleted: 10% (lock 42)"),
        Arguments.of(20, BatteryLevelCategory.CRITICAL, "Battery critical: 20% (lock 42)"),
        Arguments.of(40, BatteryLevelCategory.LOW, "Battery low: 40% (lock 42)"),
        Arguments.of(60, BatteryLevelCategory.MEDIUM, "Battery medium: 60% (lock 42)"),
        Arguments.of(80, BatteryLevelCategory.GOOD, "Battery good: 80% (lock 42)"),
        Arguments.of(81, BatteryLevelCategory.HIGH, "Battery high: 81% (lock 42)"));
  }

  @ParameterizedTest
  @MethodSource("batteryLevels")
  void should_notifyLocalizedBatteryLevel_whenHandlingReportedLevel(
      final int level, final BatteryLevelCategory category, final String message) {
    final var sut =
        new BatteryLevelReportedNotificationHandler(
            notifier, messages(), applicationLocale(Locale.ENGLISH));

    sut.handle(
        new BatteryLevelReported(42, new BatteryStatus(level, category)));

    then(notifier).should().notify(message);
  }
}
