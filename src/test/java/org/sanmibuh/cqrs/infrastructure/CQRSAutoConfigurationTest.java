package org.sanmibuh.cqrs.infrastructure;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

class CQRSAutoConfigurationTest {

  private final CQRSAutoConfiguration config = new CQRSAutoConfiguration();

  @Test
  void should_returnRegistrar_whenCqrsHandlerRegistrarCalled() {
    then(CQRSAutoConfiguration.cqrsHandlerRegistrar(new DefaultListableBeanFactory()))
        .isInstanceOf(CQRSHandlerRegistrar.class);
  }

  @Test
  void should_returnCommandBus_whenCommandHandlersProvided() {
    then(config.commandBus(List.of())).isInstanceOf(InMemoryCommandBus.class);
  }

  @Test
  void should_returnQueryBus_whenQueryHandlersProvided() {
    then(config.queryBus(List.of())).isInstanceOf(InMemoryQueryBus.class);
  }
}
