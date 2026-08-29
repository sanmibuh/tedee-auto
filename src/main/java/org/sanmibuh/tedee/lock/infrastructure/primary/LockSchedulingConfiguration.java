package org.sanmibuh.tedee.lock.infrastructure.primary;

import org.sanmibuh.cqrs.domain.CommandBus;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LockSchedulerProperties.class)
public class LockSchedulingConfiguration {

  @Bean
  LockScheduler lockScheduler(final CommandBus commandBus) {
    return new LockScheduler(commandBus);
  }

  @Bean
  LockSchedulingConfigurer lockSchedulingConfigurer(
      final LockSchedulerProperties properties, final LockScheduler lockScheduler) {
    return new LockSchedulingConfigurer(properties, lockScheduler);
  }
}
