package org.sanmibuh.tedee.lock.infrastructure.secondary;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.stream.Stream;
import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;

@ExtendWith(SoftAssertionsExtension.class)
class TedeePropertiesTest {

  private final ApplicationContextRunner runner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class))
          .withUserConfiguration(EnableTedeeProperties.class);

  @InjectSoftAssertions private BDDSoftAssertions softly;

  @Test
  void should_bindProperties_whenBaseUrlAndApiKeyAreProvided() {
    runner
        .withPropertyValues(
            "sanmibuh.rest.tedee.base-url=http://bridge.local/v1.0",
            "sanmibuh.rest.tedee.api-key=secret-token",
            "sanmibuh.rest.tedee.retry.max-retries=2",
            "sanmibuh.rest.tedee.retry.initial-interval=500",
            "sanmibuh.rest.tedee.retry.multiplier=2.0",
            "sanmibuh.rest.tedee.retry.max-interval=5000")
        .run(
            context -> {
              final var properties = context.getBean(TedeeProperties.class);
              softly.then(properties.baseUrl()).isEqualTo("http://bridge.local/v1.0");
              softly.then(properties.apiKey()).isEqualTo("secret-token");
              softly.then(properties.retry().maxRetries()).isEqualTo(2);
              softly.then(properties.retry().initialInterval()).isEqualTo(500L);
              softly.then(properties.retry().multiplier()).isEqualTo(2.0);
              softly.then(properties.retry().maxInterval()).isEqualTo(5000L);
            });
  }

  @ParameterizedTest
  @MethodSource("validBoundaryProperties")
  void should_startUp_whenRetryValuesAreAtTheirValidBoundary(final String... properties) {
    runner.withPropertyValues(properties).run(context -> then(context).hasNotFailed());
  }

  private static Stream<Arguments> validBoundaryProperties() {
    return Stream.of(
        Arguments.of(
            Named.of("max-retries is zero (retry disabled)", retry("0", "500", "2.0", "5000"))),
        Arguments.of(
            Named.of(
                "multiplier is exactly one (fixed backoff)", retry("2", "500", "1.0", "5000"))),
        Arguments.of(
            Named.of("initial-interval equals max-interval", retry("2", "5000", "2.0", "5000"))));
  }

  @ParameterizedTest
  @MethodSource("invalidProperties")
  void should_failStartup_whenPropertyIsMissingOrBlank(final String... properties) {
    runner.withPropertyValues(properties).run(context -> then(context).hasFailed());
  }

  private static String[] retry(
      final String maxRetries,
      final String initialInterval,
      final String multiplier,
      final String maxInterval) {
    return new String[] {
      "sanmibuh.rest.tedee.base-url=http://bridge.local/v1.0",
      "sanmibuh.rest.tedee.api-key=secret-token",
      "sanmibuh.rest.tedee.retry.max-retries=" + maxRetries,
      "sanmibuh.rest.tedee.retry.initial-interval=" + initialInterval,
      "sanmibuh.rest.tedee.retry.multiplier=" + multiplier,
      "sanmibuh.rest.tedee.retry.max-interval=" + maxInterval
    };
  }

  private static Stream<Arguments> invalidProperties() {
    return Stream.of(
        Arguments.of(
            Named.of(
                "base-url is missing", new String[] {"sanmibuh.rest.tedee.api-key=secret-token"})),
        Arguments.of(
            Named.of(
                "api-key is missing",
                new String[] {"sanmibuh.rest.tedee.base-url=http://bridge.local/v1.0"})),
        Arguments.of(
            Named.of(
                "base-url is blank",
                new String[] {
                  "sanmibuh.rest.tedee.base-url=  ", "sanmibuh.rest.tedee.api-key=secret-token"
                })),
        Arguments.of(
            Named.of(
                "api-key is blank",
                new String[] {
                  "sanmibuh.rest.tedee.base-url=http://bridge.local/v1.0",
                  "sanmibuh.rest.tedee.api-key=  "
                })),
        Arguments.of(
            Named.of(
                "retry is missing",
                new String[] {
                  "sanmibuh.rest.tedee.base-url=http://bridge.local/v1.0",
                  "sanmibuh.rest.tedee.api-key=secret-token"
                })),
        Arguments.of(Named.of("max-retries is negative", retry("-1", "500", "2.0", "5000"))),
        Arguments.of(Named.of("initial-interval is not positive", retry("2", "0", "2.0", "5000"))),
        Arguments.of(Named.of("multiplier is below one", retry("2", "500", "0.5", "5000"))),
        Arguments.of(Named.of("max-interval is not positive", retry("2", "500", "2.0", "0"))),
        Arguments.of(
            Named.of("initial-interval exceeds max-interval", retry("2", "6000", "2.0", "5000"))));
  }

  @EnableConfigurationProperties(TedeeProperties.class)
  private static final class EnableTedeeProperties {}
}
