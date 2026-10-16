package org.sanmibuh.ddd.adapter;

import org.sanmibuh.ddd.port.CommandBus;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
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
    if (BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, CommandBus.class).length
        > 0) {
      return;
    }

    registry.registerBeanDefinition(
        DddAutoConfiguration.IN_MEMORY_COMMAND_BUS_BEAN_NAME,
        createInMemoryCommandBusDefinitionPITEquivalent());
  }

  private RootBeanDefinition createInMemoryCommandBusDefinitionPITEquivalent() {
    var definition = new RootBeanDefinition(InMemoryCommandBus.class);
    definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
    definition.setAttribute(DddAutoConfiguration.IN_MEMORY_COMMAND_BUS_MARKER, true);
    return definition;
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
    var transactionManagerNames =
        BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
            beanFactory, PlatformTransactionManager.class);
    return transactionManagerNames.length == 1
        || hasSinglePrimaryTransactionManager(beanFactory, transactionManagerNames);
  }

  private boolean hasSinglePrimaryTransactionManager(
      final ConfigurableListableBeanFactory beanFactory, final String[] transactionManagerNames) {
    var primaryTransactionManagerCount = 0;
    for (final var transactionManagerName : transactionManagerNames) {
      if (isPrimary(beanFactory, transactionManagerName)) {
        primaryTransactionManagerCount++;
      }
    }
    return hasSinglePrimaryTransactionManagerPITEquivalent(primaryTransactionManagerCount);
  }

  private boolean hasSinglePrimaryTransactionManagerPITEquivalent(
      final int primaryTransactionManagerCount) {
    return primaryTransactionManagerCount == 1;
  }

  private boolean isPrimary(
      final ConfigurableListableBeanFactory beanFactory, final String transactionManagerName) {
    if (beanFactory.containsBeanDefinition(transactionManagerName)) {
      return beanFactory.getBeanDefinition(transactionManagerName).isPrimary();
    }

    final var parentBeanFactory = beanFactory.getParentBeanFactory();
    return parentBeanFactory
            instanceof ConfigurableListableBeanFactory parentConfigurableBeanFactory
        && parentConfigurableBeanFactory.containsBeanDefinition(transactionManagerName)
        && parentConfigurableBeanFactory.getBeanDefinition(transactionManagerName).isPrimary();
  }

  private boolean hasTransactionalCommandBus(final ConfigurableListableBeanFactory beanFactory) {
    return beanFactory.getBeanNamesForType(TransactionalCommandBus.class).length > 0;
  }
}
