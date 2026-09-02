package org.sanmibuh.ddd.infrastructure;

import static org.assertj.core.api.BDDAssertions.then;

import com.example.StubEventHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;

class DomainEventHandlerRegistrarTest {

  @Test
  void should_notRegisterAnyBean_whenAutoConfigurationPackagesNotPresent() {
    final var beanFactory = new DefaultListableBeanFactory();
    final var registrar = new DomainEventHandlerRegistrar(beanFactory);

    registrar.postProcessBeanDefinitionRegistry(beanFactory);

    then(beanFactory.getBeanDefinitionCount()).isZero();
  }

  @Test
  void should_registerEventHandlerBean_whenFoundInAutoConfigurationPackage() {
    final var beanFactory = new DefaultListableBeanFactory();
    AutoConfigurationPackages.register(beanFactory, "com.example");
    final var registrar = new DomainEventHandlerRegistrar(beanFactory);

    registrar.postProcessBeanDefinitionRegistry(beanFactory);

    then(beanFactory.containsBeanDefinition("stubEventHandler")).isTrue();
  }

  @Test
  void should_notRegisterHandlerBean_whenAlreadyRegistered() {
    final var beanFactory = new DefaultListableBeanFactory();
    AutoConfigurationPackages.register(beanFactory, "com.example");
    beanFactory.registerBeanDefinition(
        "myEventHandler", new RootBeanDefinition(StubEventHandler.class));

    final var registrar = new DomainEventHandlerRegistrar(beanFactory);
    registrar.postProcessBeanDefinitionRegistry(beanFactory);

    then(beanFactory.getBeansOfType(StubEventHandler.class)).hasSize(1);
  }
}
