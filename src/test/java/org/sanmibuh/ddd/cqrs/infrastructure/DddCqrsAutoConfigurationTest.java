package org.sanmibuh.ddd.cqrs.infrastructure;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.sanmibuh.ddd.infrastructure.InMemoryEventBus;

class DddCqrsAutoConfigurationTest {

  private final DddCqrsAutoConfiguration config = new DddCqrsAutoConfiguration();

  @Test
  void should_returnPublishingCommandBus_whenCommandHandlersAndEventBusProvided() {
    then(config.commandBus(List.of(), new InMemoryEventBus(List.of())))
        .isInstanceOf(DomainEventPublishingCommandBus.class);
  }
}
