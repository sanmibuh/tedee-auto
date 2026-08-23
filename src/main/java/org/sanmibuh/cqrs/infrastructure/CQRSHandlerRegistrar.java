package org.sanmibuh.cqrs.infrastructure;

import java.util.List;
import java.util.Objects;

import org.sanmibuh.cqrs.domain.CommandHandler;
import org.sanmibuh.cqrs.domain.QueryHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

class CQRSHandlerRegistrar implements BeanDefinitionRegistryPostProcessor, BeanFactoryAware {

  private BeanFactory beanFactory;

  @Override
  public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }

  @Override
  public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry) throws BeansException {
    final var packages = AutoConfigurationPackages.get(beanFactory);
    registerHandlers(registry, packages, CommandHandler.class);
    registerHandlers(registry, packages, QueryHandler.class);
  }

  private void registerHandlers(
    final BeanDefinitionRegistry registry,
    final List<String> packages,
    final Class<?> handlerType) {
    final var scanner = new ClassPathScanningCandidateComponentProvider(false);
    scanner.addIncludeFilter(new AssignableTypeFilter(handlerType));
    packages.stream()
      .flatMap(basePackage -> scanner.findCandidateComponents(basePackage).stream())
      .map(BeanDefinition::getBeanClassName)
      .filter(Objects::nonNull)
      .forEach(beanClassName -> registry.registerBeanDefinition(beanClassName, new RootBeanDefinition(beanClassName)));
  }
}
