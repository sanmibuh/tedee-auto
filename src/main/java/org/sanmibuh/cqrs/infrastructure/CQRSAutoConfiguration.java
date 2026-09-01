package org.sanmibuh.cqrs.infrastructure;

import java.util.List;
import org.sanmibuh.cqrs.port.BaseCommandHandler;
import org.sanmibuh.cqrs.port.CommandBus;
import org.sanmibuh.cqrs.port.QueryBus;
import org.sanmibuh.cqrs.port.QueryHandler;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class CQRSAutoConfiguration {

  @Bean
  static CQRSHandlerRegistrar cqrsHandlerRegistrar(final BeanFactory beanFactory) {
    return new CQRSHandlerRegistrar(beanFactory);
  }

  @Bean
  @ConditionalOnMissingBean(CommandBus.class)
  public InMemoryCommandBus commandBus(final List<BaseCommandHandler<?, ?>> handlers) {
    return new InMemoryCommandBus(handlers);
  }

  @Bean
  @ConditionalOnMissingBean(QueryBus.class)
  public InMemoryQueryBus queryBus(final List<QueryHandler<?, ?>> handlers) {
    return new InMemoryQueryBus(handlers);
  }
}
