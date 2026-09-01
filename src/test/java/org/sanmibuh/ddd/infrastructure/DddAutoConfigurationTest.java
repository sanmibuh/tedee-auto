package org.sanmibuh.ddd.infrastructure;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

class DddAutoConfigurationTest {

  private final DddAutoConfiguration config = new DddAutoConfiguration();

  @Test
  void should_returnRegistrar_whenDomainEventHandlerRegistrarCalled() {
    then(DddAutoConfiguration.domainEventHandlerRegistrar(new DefaultListableBeanFactory()))
        .isInstanceOf(DomainEventHandlerRegistrar.class);
  }

  @Test
  void should_returnEventBus_whenDomainEventHandlersProvided() {
    then(config.eventBus(List.of())).isInstanceOf(InMemoryEventBus.class);
  }
}
