package org.sanmibuh.ddd.infrastructure;

import static org.assertj.core.api.BDDAssertions.then;

import com.example.StubCommandHandler;
import com.example.StubComponentCommandHandler;
import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;

@ExtendWith(SoftAssertionsExtension.class)
class CQRSHandlerRegistrarTest {

  @InjectSoftAssertions private BDDSoftAssertions softly;

  @Test
  void should_notRegisterAnyBean_whenAutoConfigurationPackagesNotPresent() {
    final var beanFactory = new DefaultListableBeanFactory();
    final var sut = new CQRSHandlerRegistrar(beanFactory);

    sut.postProcessBeanDefinitionRegistry(beanFactory);

    then(beanFactory.getBeanDefinitionCount()).isZero();
  }

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
        "stubComponentCommandHandler", new RootBeanDefinition(StubComponentCommandHandler.class));

    final var sut = new CQRSHandlerRegistrar(beanFactory);
    sut.postProcessBeanDefinitionRegistry(beanFactory);

    softly
        .then(beanFactory.getBeanDefinition("stubComponentCommandHandler").getBeanClassName())
        .isEqualTo(StubComponentCommandHandler.class.getName());
    softly.then(beanFactory.getBeansOfType(StubCommandHandler.class)).hasSize(1);
    softly.then(beanFactory.getBeansOfType(StubComponentCommandHandler.class)).hasSize(1);
  }

  @Test
  void should_notRegisterHandlerBean_whenAlreadyRegisteredUnderCustomName() {
    final var beanFactory = new DefaultListableBeanFactory();
    AutoConfigurationPackages.register(beanFactory, "com.example");
    beanFactory.registerBeanDefinition(
        "myCustomHandlerName", new RootBeanDefinition(StubComponentCommandHandler.class));

    final var sut = new CQRSHandlerRegistrar(beanFactory);
    sut.postProcessBeanDefinitionRegistry(beanFactory);

    softly.then(beanFactory.getBeansOfType(StubComponentCommandHandler.class)).hasSize(1);
    softly.then(beanFactory.containsBeanDefinition("myCustomHandlerName")).isTrue();
  }

  @SuppressWarnings("SameParameterValue")
  private DefaultListableBeanFactory registrarAppliedTo(final String basePackage) {
    final var beanFactory = new DefaultListableBeanFactory();
    AutoConfigurationPackages.register(beanFactory, basePackage);
    final var sut = new CQRSHandlerRegistrar(beanFactory);
    sut.postProcessBeanDefinitionRegistry(beanFactory);
    return beanFactory;
  }
}
