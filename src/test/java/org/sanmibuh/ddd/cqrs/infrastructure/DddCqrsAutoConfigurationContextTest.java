package org.sanmibuh.ddd.cqrs.infrastructure;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.sanmibuh.cqrs.infrastructure.CQRSAutoConfiguration;
import org.sanmibuh.cqrs.port.CommandBus;
import org.sanmibuh.ddd.infrastructure.DddAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class DddCqrsAutoConfigurationContextTest {

  private final ApplicationContextRunner sut =
      new ApplicationContextRunner()
          .withConfiguration(
              AutoConfigurations.of(
                  CQRSAutoConfiguration.class,
                  DddAutoConfiguration.class,
                  DddCqrsAutoConfiguration.class));

  @Test
  void should_useThePublishingCommandBus_whenBothAutoConfigurationsAreActive() {
    sut.run(
        context ->
            then(context)
                .getBean(CommandBus.class)
                .isInstanceOf(DomainEventPublishingCommandBus.class));
  }
}
