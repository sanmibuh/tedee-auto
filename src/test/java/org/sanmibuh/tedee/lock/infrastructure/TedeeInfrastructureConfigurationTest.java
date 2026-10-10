package org.sanmibuh.tedee.lock.infrastructure;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class TedeeInfrastructureConfigurationTest {

  private final ApplicationContextRunner runner =
      new ApplicationContextRunner()
          .withUserConfiguration(TedeeInfrastructureConfiguration.class)
          .withPropertyValues(
              "sanmibuh.rest.tedee.base-url=http://bridge.local/v1.0",
              "sanmibuh.rest.tedee.api-key=secret-token",
              "sanmibuh.rest.tedee.callback-secret=callback-secret",
              "sanmibuh.rest.tedee.callback-registration-interval=30s",
              "sanmibuh.rest.tedee.retry.max-retries=0",
              "sanmibuh.rest.tedee.retry.initial-interval=1",
              "sanmibuh.rest.tedee.retry.multiplier=1",
              "sanmibuh.rest.tedee.retry.max-interval=1");

  @Test
  void should_registerTedeeProperties_whenConfigurationIsLoaded() {
    runner.run(context -> then(context).hasSingleBean(TedeeProperties.class));
  }
}
