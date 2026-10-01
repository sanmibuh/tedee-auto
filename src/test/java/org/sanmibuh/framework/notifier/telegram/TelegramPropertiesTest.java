package org.sanmibuh.framework.notifier.telegram;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.stream.Stream;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;

class TelegramPropertiesTest {

  private final ApplicationContextRunner runner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class))
          .withUserConfiguration(TelegramNotificationConfiguration.class);

  @ParameterizedTest
  @MethodSource("invalidProperties")
  void should_failStartup_whenPropertyIsMissingOrBlank(final String... properties) {
    runner.withPropertyValues(properties).run(context -> then(context).hasFailed());
  }

  private static Stream<Arguments> invalidProperties() {
    return Stream.of(
        Arguments.of(
            Named.of(
                "base-url is missing",
                new String[] {
                  "sanmibuh.notification.telegram.bot-token=bot-token",
                  "sanmibuh.notification.telegram.chat-id=chat-id"
                })),
        Arguments.of(
            Named.of(
                "bot-token is missing",
                new String[] {
                  "sanmibuh.notification.telegram.base-url=http://telegram.local",
                  "sanmibuh.notification.telegram.chat-id=chat-id"
                })),
        Arguments.of(
            Named.of(
                "chat-id is missing",
                new String[] {
                  "sanmibuh.notification.telegram.base-url=http://telegram.local",
                  "sanmibuh.notification.telegram.bot-token=bot-token"
                })),
        Arguments.of(
            Named.of(
                "base-url is blank",
                new String[] {
                  "sanmibuh.notification.telegram.base-url=  ",
                  "sanmibuh.notification.telegram.bot-token=bot-token",
                  "sanmibuh.notification.telegram.chat-id=chat-id"
                })),
        Arguments.of(
            Named.of(
                "bot-token is blank",
                new String[] {
                  "sanmibuh.notification.telegram.base-url=http://telegram.local",
                  "sanmibuh.notification.telegram.bot-token=  ",
                  "sanmibuh.notification.telegram.chat-id=chat-id"
                })),
        Arguments.of(
            Named.of(
                "chat-id is blank",
                new String[] {
                  "sanmibuh.notification.telegram.base-url=http://telegram.local",
                  "sanmibuh.notification.telegram.bot-token=bot-token",
                  "sanmibuh.notification.telegram.chat-id=  "
                })));
  }
}
