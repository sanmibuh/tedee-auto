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
            "sanmibuh.rest.tedee.api-key=secret-token")
        .run(
            context -> {
              final var properties = context.getBean(TedeeProperties.class);
              softly.then(properties.baseUrl()).isEqualTo("http://bridge.local/v1.0");
              softly.then(properties.apiKey()).isEqualTo("secret-token");
            });
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
                })));
  }

  @EnableConfigurationProperties(TedeeProperties.class)
  private static final class EnableTedeeProperties {}
}
