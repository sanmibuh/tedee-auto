package org.sanmibuh.ddd.infrastructure;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

class DomainEventHandlerRegistrar implements BeanDefinitionRegistryPostProcessor {

  DomainEventHandlerRegistrar(final BeanFactory beanFactory) {}

  @Override
  public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry)
      throws BeansException {}
}
