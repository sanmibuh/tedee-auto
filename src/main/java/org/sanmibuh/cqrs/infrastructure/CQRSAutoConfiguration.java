package org.sanmibuh.cqrs.infrastructure;

import java.util.List;
import org.sanmibuh.cqrs.domain.CommandBus;
import org.sanmibuh.cqrs.domain.CommandHandler;
import org.sanmibuh.cqrs.domain.QueryBus;
import org.sanmibuh.cqrs.domain.QueryHandler;
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
  public SimpleCommandBus commandBus(final List<CommandHandler<?>> handlers) {
    return new SimpleCommandBus(handlers);
  }

  @Bean
  @ConditionalOnMissingBean(QueryBus.class)
  public SimpleQueryBus queryBus(final List<QueryHandler<?, ?>> handlers) {
    return new SimpleQueryBus(handlers);
  }
}
