package org.sanmibuh.tedee;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;

class ServerPropertiesTest {

  private final ApplicationContextRunner runner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class))
          .withUserConfiguration(ServerConfiguration.class);

  @ParameterizedTest
  @ValueSource(strings = {"sanmibuh.server.other=value", "sanmibuh.server.public-url=  "})
  void should_failStartup_whenPublicUrlIsMissingOrBlank(final String property) {
    runner.withPropertyValues(property).run(context -> then(context).hasFailed());
  }
}
