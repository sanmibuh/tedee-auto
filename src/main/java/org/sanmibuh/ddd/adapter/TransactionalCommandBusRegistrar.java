package org.sanmibuh.ddd.adapter;

import org.sanmibuh.ddd.port.CommandBus;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
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
    BeanFactory currentBeanFactory = beanFactory;
    while (currentBeanFactory instanceof ConfigurableListableBeanFactory configurableBeanFactory) {
      final var localTransactionManagerNames =
          configurableBeanFactory.getBeanNamesForType(
              PlatformTransactionManager.class, false, false);
      if (localTransactionManagerNames.length > 0) {
        return localTransactionManagerNames.length == 1
            || hasSinglePrimaryTransactionManagerPITEquivalent(
                countPrimary(configurableBeanFactory, localTransactionManagerNames));
      }
      currentBeanFactory = configurableBeanFactory.getParentBeanFactory();
    }
    return false;
  }

  private int countPrimary(
      final ConfigurableListableBeanFactory beanFactory, final String[] beanNames) {
    var primaryCount = 0;
    for (final var beanName : beanNames) {
      if (isPrimary(beanFactory, beanName)) {
        primaryCount++;
      }
    }
    return primaryCount;
  }

  private boolean isPrimary(
      final ConfigurableListableBeanFactory beanFactory, final String beanName) {
    return beanFactory.containsBeanDefinition(beanName)
        && beanFactory.getBeanDefinition(beanName).isPrimary();
  }

  private boolean hasSinglePrimaryTransactionManagerPITEquivalent(
      final int primaryTransactionManagerCount) {
    return primaryTransactionManagerCount == 1;
  }

  private boolean hasTransactionalCommandBus(final ConfigurableListableBeanFactory beanFactory) {
    return beanFactory.getBeanNamesForType(TransactionalCommandBus.class).length > 0;
  }
}
