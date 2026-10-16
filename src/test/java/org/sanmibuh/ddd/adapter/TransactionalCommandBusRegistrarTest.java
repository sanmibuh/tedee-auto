package org.sanmibuh.ddd.adapter;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Optional;
import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.transaction.PlatformTransactionManager;

@ExtendWith(SoftAssertionsExtension.class)
class TransactionalCommandBusRegistrarTest {

  private final TransactionalCommandBusRegistrar sut = new TransactionalCommandBusRegistrar();
  @InjectSoftAssertions private BDDSoftAssertions softly;

  @Test
  void should_registerTransactionalCommandBus_whenInMemoryBusAndTransactionManagerProvided() {
    final var beanFactory = new DefaultListableBeanFactory();
    beanFactory.registerBeanDefinition(
        "transactionManager", new RootBeanDefinition(PlatformTransactionManager.class));

    sut.postProcessBeanDefinitionRegistry(beanFactory);

    final var definition =
        beanFactory.getBeanDefinition(DddAutoConfiguration.TRANSACTIONAL_COMMAND_BUS_BEAN_NAME);
    final var constructorArgument =
        definition
            .getConstructorArgumentValues()
            .getIndexedArgumentValue(0, RuntimeBeanReference.class);
    final var beanReference =
        Optional.ofNullable(constructorArgument)
            .map(ValueHolder::getValue)
            .filter(RuntimeBeanReference.class::isInstance)
            .map(RuntimeBeanReference.class::cast);
    softly.then(definition.isPrimary()).isTrue();
    softly.then(beanReference).isPresent();
    softly
        .then(beanReference.orElseThrow().getBeanName())
        .isEqualTo(DddAutoConfiguration.IN_MEMORY_COMMAND_BUS_BEAN_NAME);
  }

  @Test
  void should_notRegisterTransactionalCommandBus_whenTransactionManagerMissing() {
    final var beanFactory = new DefaultListableBeanFactory();

    sut.postProcessBeanDefinitionRegistry(beanFactory);

    then(beanFactory.containsBeanDefinition(
            DddAutoConfiguration.TRANSACTIONAL_COMMAND_BUS_BEAN_NAME))
        .isFalse();
  }

  @Test
  void should_notRegisterTransactionalCommandBus_whenMultipleTransactionManagersProvided() {
    final var beanFactory = new DefaultListableBeanFactory();
    beanFactory.registerBeanDefinition(
        "firstTransactionManager", new RootBeanDefinition(PlatformTransactionManager.class));
    beanFactory.registerBeanDefinition(
        "secondTransactionManager", new RootBeanDefinition(PlatformTransactionManager.class));

    sut.postProcessBeanDefinitionRegistry(beanFactory);

    then(beanFactory.containsBeanDefinition(
            DddAutoConfiguration.TRANSACTIONAL_COMMAND_BUS_BEAN_NAME))
        .isFalse();
  }

  @Test
  void should_registerTransactionalCommandBus_whenPrimaryTransactionManagerProvided() {
    final var beanFactory = new DefaultListableBeanFactory();
    final var primaryTransactionManager = new RootBeanDefinition(PlatformTransactionManager.class);
    primaryTransactionManager.setPrimary(true);
    beanFactory.registerBeanDefinition("primaryTransactionManager", primaryTransactionManager);
    beanFactory.registerBeanDefinition(
        "secondaryTransactionManager", new RootBeanDefinition(PlatformTransactionManager.class));

    sut.postProcessBeanDefinitionRegistry(beanFactory);

    then(beanFactory.containsBeanDefinition(
            DddAutoConfiguration.TRANSACTIONAL_COMMAND_BUS_BEAN_NAME))
        .isTrue();
  }

  @Test
  void should_registerTransactionalCommandBus_whenParentProvidesPrimaryTransactionManager() {
    final var parentBeanFactory = new DefaultListableBeanFactory();
    final var primaryTransactionManager = new RootBeanDefinition(PlatformTransactionManager.class);
    primaryTransactionManager.setPrimary(true);
    parentBeanFactory.registerBeanDefinition(
        "primaryTransactionManager", primaryTransactionManager);
    final var beanFactory = new DefaultListableBeanFactory(parentBeanFactory);
    beanFactory.registerBeanDefinition(
        "secondaryTransactionManager", new RootBeanDefinition(PlatformTransactionManager.class));

    sut.postProcessBeanDefinitionRegistry(beanFactory);

    then(beanFactory.containsBeanDefinition(
            DddAutoConfiguration.TRANSACTIONAL_COMMAND_BUS_BEAN_NAME))
        .isTrue();
  }

  @Test
  void should_notRegisterTransactionalCommandBus_whenParentTransactionManagerIsNotPrimary() {
    final var parentBeanFactory = new DefaultListableBeanFactory();
    parentBeanFactory.registerBeanDefinition(
        "parentTransactionManager", new RootBeanDefinition(PlatformTransactionManager.class));
    final var beanFactory = new DefaultListableBeanFactory(parentBeanFactory);
    beanFactory.registerBeanDefinition(
        "childTransactionManager", new RootBeanDefinition(PlatformTransactionManager.class));

    sut.postProcessBeanDefinitionRegistry(beanFactory);

    then(beanFactory.containsBeanDefinition(
            DddAutoConfiguration.TRANSACTIONAL_COMMAND_BUS_BEAN_NAME))
        .isFalse();
  }

  @Test
  void should_notRegisterTransactionalCommandBus_whenMultiplePrimaryTransactionManagersProvided() {
    final var beanFactory = new DefaultListableBeanFactory();
    final var firstTransactionManager = new RootBeanDefinition(PlatformTransactionManager.class);
    firstTransactionManager.setPrimary(true);
    beanFactory.registerBeanDefinition("firstTransactionManager", firstTransactionManager);
    final var secondTransactionManager = new RootBeanDefinition(PlatformTransactionManager.class);
    secondTransactionManager.setPrimary(true);
    beanFactory.registerBeanDefinition("secondTransactionManager", secondTransactionManager);

    sut.postProcessBeanDefinitionRegistry(beanFactory);

    then(beanFactory.containsBeanDefinition(
            DddAutoConfiguration.TRANSACTIONAL_COMMAND_BUS_BEAN_NAME))
        .isFalse();
  }
}
