package org.sanmibuh.ddd.infrastructure;

import java.util.List;
import org.sanmibuh.ddd.port.DomainEventHandler;
import org.sanmibuh.ddd.port.EventBus;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class DddAutoConfiguration {

  @Bean
  static DomainEventHandlerRegistrar domainEventHandlerRegistrar(final BeanFactory beanFactory) {
    return new DomainEventHandlerRegistrar(beanFactory);
  }

  @Bean
  @ConditionalOnMissingBean(EventBus.class)
  public InMemoryEventBus eventBus(final List<DomainEventHandler<?>> handlers) {
    return new InMemoryEventBus(handlers);
  }
}
