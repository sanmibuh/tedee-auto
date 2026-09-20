package org.sanmibuh.tedee.lock.infrastructure.primary;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class LockSchedulerPropertiesTest {

  private final ApplicationContextRunner runner =
      new ApplicationContextRunner().withUserConfiguration(EnableLockSchedulerProperties.class);

  @Test
  void should_bindZoneToUtc_whenZoneIsNotProvided() {
    runner.run(
        context -> {
          final var properties = context.getBean(LockSchedulerProperties.class);
          then(properties.zone()).isEqualTo(LockSchedulerProperties.DEFAULT_ZONE);
        });
  }

  @EnableConfigurationProperties(LockSchedulerProperties.class)
  private static final class EnableLockSchedulerProperties {}
}
