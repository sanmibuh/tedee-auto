package org.sanmibuh.ddd.adapter;

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
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@AutoConfiguration
public class DddAutoConfiguration {

  static final String IN_MEMORY_COMMAND_BUS_BEAN_NAME = "inMemoryCommandBus";
  static final String TRANSACTIONAL_COMMAND_BUS_BEAN_NAME = "transactionalCommandBus";

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
  static BeanDefinitionRegistryPostProcessor transactionalCommandBusRegistrar() {
    return new TransactionalCommandBusRegistrar();
  }

  @Bean
  @ConditionalOnMissingBean(EventBus.class)
  public InMemoryEventBus eventBus(final List<DomainEventHandler<?>> handlers) {
    return new InMemoryEventBus(handlers);
  }

  @Bean(IN_MEMORY_COMMAND_BUS_BEAN_NAME)
  @ConditionalOnMissingBean(CommandBus.class)
  public InMemoryCommandBus inMemoryCommandBus(
      final List<CommandHandler<?, ?>> handlers, final EventBus eventBus) {
    return new InMemoryCommandBus(handlers, eventBus);
  }

  @Bean
  @ConditionalOnMissingBean(QueryBus.class)
  public InMemoryQueryBus queryBus(final List<QueryHandler<?, ?>> handlers) {
    return new InMemoryQueryBus(handlers);
  }

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnBean(PlatformTransactionManager.class)
  @EnableTransactionManagement
  static class TransactionManagementConfiguration {}
}
