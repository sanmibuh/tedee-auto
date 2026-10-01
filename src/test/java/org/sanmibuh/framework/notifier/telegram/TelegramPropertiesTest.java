package org.sanmibuh.framework.notifier.telegram;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.stream.Stream;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.restclient.autoconfigure.RestClientAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;

class TelegramPropertiesTest {

  private final ApplicationContextRunner runner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class))
          .withConfiguration(AutoConfigurations.of(RestClientAutoConfiguration.class))
          .withUserConfiguration(TelegramNotificationConfiguration.class);

  @Test
  void should_startUp_whenPropertiesAreValid() {
    runner.withPropertyValues(validProperties()).run(context -> then(context).hasNotFailed());
  }

  @ParameterizedTest
  @MethodSource("invalidProperties")
  void should_failStartup_whenPropertyIsMissingOrBlank(final String... properties) {
    runner.withPropertyValues(properties).run(context -> then(context).hasFailed());
  }

  private static Stream<Arguments> invalidProperties() {
    return Stream.of(
        Arguments.of(
            Named.of(
                "retry is missing",
                new String[] {
                  "sanmibuh.notification.telegram.base-url=http://telegram.local",
                  "sanmibuh.notification.telegram.bot-token=bot-token",
                  "sanmibuh.notification.telegram.chat-id=chat-id"
                })),
        Arguments.of(
            Named.of(
                "max-retries is not positive",
                properties("sanmibuh.notification.telegram.retry.max-retries=0"))),
        Arguments.of(
            Named.of(
                "delay is not positive",
                properties("sanmibuh.notification.telegram.retry.delay=0"))),
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

  private static String[] validProperties() {
    return properties();
  }

  private static String[] properties(final String... overrides) {
    final var properties =
        Stream.concat(
                Stream.of(
                    "sanmibuh.notification.telegram.base-url=http://telegram.local",
                    "sanmibuh.notification.telegram.bot-token=bot-token",
                    "sanmibuh.notification.telegram.chat-id=chat-id",
                    "sanmibuh.notification.telegram.retry.max-retries=2",
                    "sanmibuh.notification.telegram.retry.delay=500"),
                Stream.of(overrides))
            .toList();
    return properties.toArray(String[]::new);
  }
}
