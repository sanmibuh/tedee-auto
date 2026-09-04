package org.sanmibuh.ddd.infrastructure;

import java.util.List;
import org.sanmibuh.ddd.port.CommandBus;
import org.sanmibuh.ddd.port.CommandHandler;
import org.sanmibuh.ddd.port.DomainEventHandler;
import org.sanmibuh.ddd.port.EventBus;
import org.sanmibuh.ddd.port.QueryBus;
import org.sanmibuh.ddd.port.QueryHandler;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class DddAutoConfiguration {

  @Bean
  static BeanDefinitionRegistryPostProcessor domainEventHandlerRegistrar(
      final BeanFactory beanFactory) {
    return new DomainEventHandlerRegistrar(beanFactory);
  }

  @Bean
  static BeanDefinitionRegistryPostProcessor cqrsHandlerRegistrar(final BeanFactory beanFactory) {
    return new CQRSHandlerRegistrar(beanFactory);
  }

  @Bean
  @ConditionalOnMissingBean(EventBus.class)
  public InMemoryEventBus eventBus(final List<DomainEventHandler<?>> handlers) {
    return new InMemoryEventBus(handlers);
  }

  @Bean
  @ConditionalOnMissingBean(CommandBus.class)
  public InMemoryCommandBus commandBus(
      final List<CommandHandler<?, ?>> handlers, final EventBus eventBus) {
    return new InMemoryCommandBus(handlers, eventBus);
  }

  @Bean
  @ConditionalOnMissingBean(QueryBus.class)
  public InMemoryQueryBus queryBus(final List<QueryHandler<?, ?>> handlers) {
    return new InMemoryQueryBus(handlers);
  }
}
