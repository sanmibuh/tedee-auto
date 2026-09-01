package org.sanmibuh.ddd.cqrs.infrastructure;

import java.util.List;
import org.sanmibuh.cqrs.port.BaseCommandHandler;
import org.sanmibuh.cqrs.port.CommandBus;
import org.sanmibuh.ddd.port.EventBus;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class DddCqrsAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(CommandBus.class)
  public DomainEventPublishingCommandBus commandBus(
      final List<BaseCommandHandler<?, ?>> handlers, final EventBus eventBus) {
    throw new UnsupportedOperationException();
  }
}
