package org.sanmibuh.cqrs.infrastructure;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;

import com.example.StubCommandHandler;
import com.example.StubComponentCommandHandler;

class CQRSHandlerRegistrarTest {

  @Test
  void should_registerCommandHandlerBean_whenFoundInAutoConfigurationPackage() {
    final var beanFactory = registrarAppliedTo("com.example");

    then(beanFactory.containsBeanDefinition("stubCommandHandler")).isTrue();
  }

  @Test
  void should_registerQueryHandlerBean_whenFoundInAutoConfigurationPackage() {
    final var beanFactory = registrarAppliedTo("com.example");

    then(beanFactory.containsBeanDefinition("stubQueryHandler")).isTrue();
  }

  @Test
  void should_notRegisterHandlerBean_whenAlreadyRegisteredViaComponentScan() {
    final var beanFactory = new DefaultListableBeanFactory();
    AutoConfigurationPackages.register(beanFactory, "com.example");
    beanFactory.registerBeanDefinition(
      "stubComponentCommandHandler",
      new RootBeanDefinition(StubComponentCommandHandler.class));

    final var registrar = new CQRSHandlerRegistrar();
    registrar.setBeanFactory(beanFactory);
    registrar.postProcessBeanDefinitionRegistry(beanFactory);

    then(beanFactory.getBeanDefinition("stubComponentCommandHandler").getBeanClassName())
      .isEqualTo(StubComponentCommandHandler.class.getName());
    then(beanFactory.getBeansOfType(StubCommandHandler.class)).hasSize(1);
    then(beanFactory.getBeansOfType(StubComponentCommandHandler.class)).hasSize(1);
  }

  @SuppressWarnings("SameParameterValue")
  private DefaultListableBeanFactory registrarAppliedTo(final String basePackage) {
    final var beanFactory = new DefaultListableBeanFactory();
    AutoConfigurationPackages.register(beanFactory, basePackage);
    final var registrar = new CQRSHandlerRegistrar();
    registrar.setBeanFactory(beanFactory);
    registrar.postProcessBeanDefinitionRegistry(beanFactory);
    return beanFactory;
  }
}
