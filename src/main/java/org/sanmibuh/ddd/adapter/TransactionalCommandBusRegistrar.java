package org.sanmibuh.ddd.adapter;

import org.sanmibuh.ddd.port.CommandBus;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.transaction.PlatformTransactionManager;

final class TransactionalCommandBusRegistrar implements BeanDefinitionRegistryPostProcessor {

  @Override
  public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry) {
    if (!(registry instanceof ConfigurableListableBeanFactory beanFactory)) {
      return;
    }

    registerInMemoryCommandBusIfAbsent(registry, beanFactory);
    if (!hasInMemoryCommandBus(beanFactory)
        || !hasTransactionManager(beanFactory)
        || hasTransactionalCommandBus(beanFactory)) {
      return;
    }

    var definition = new RootBeanDefinition(InMemoryTransactionalCommandBus.class);
    definition.setPrimary(true);
    definition
        .getConstructorArgumentValues()
        .addIndexedArgumentValue(
            0, new RuntimeBeanReference(DddAutoConfiguration.IN_MEMORY_COMMAND_BUS_BEAN_NAME));
    registry.registerBeanDefinition(
        DddAutoConfiguration.TRANSACTIONAL_COMMAND_BUS_BEAN_NAME, definition);
  }

  private void registerInMemoryCommandBusIfAbsent(
      final BeanDefinitionRegistry registry, final ConfigurableListableBeanFactory beanFactory) {
    if (beanFactory.getBeanNamesForType(CommandBus.class).length > 0) {
      return;
    }

    var definition = new RootBeanDefinition(InMemoryCommandBus.class);
    definition.setAutowireMode(RootBeanDefinition.AUTOWIRE_CONSTRUCTOR);
    definition.setAttribute(DddAutoConfiguration.IN_MEMORY_COMMAND_BUS_MARKER, true);
    registry.registerBeanDefinition(
        DddAutoConfiguration.IN_MEMORY_COMMAND_BUS_BEAN_NAME, definition);
  }

  @Override
  public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory)
      throws BeansException {
    // No-op
  }

  private boolean hasInMemoryCommandBus(final ConfigurableListableBeanFactory beanFactory) {
    if (!beanFactory.containsBeanDefinition(DddAutoConfiguration.IN_MEMORY_COMMAND_BUS_BEAN_NAME)) {
      return false;
    }

    var beanDefinition =
        beanFactory.getBeanDefinition(DddAutoConfiguration.IN_MEMORY_COMMAND_BUS_BEAN_NAME);
    return beanDefinition.getAttribute(DddAutoConfiguration.IN_MEMORY_COMMAND_BUS_MARKER) != null;
  }

  private boolean hasTransactionManager(final ConfigurableListableBeanFactory beanFactory) {
    return beanFactory.getBeanNamesForType(PlatformTransactionManager.class).length == 1;
  }

  private boolean hasTransactionalCommandBus(final ConfigurableListableBeanFactory beanFactory) {
    return beanFactory.getBeanNamesForType(TransactionalCommandBus.class).length > 0;
  }
}
