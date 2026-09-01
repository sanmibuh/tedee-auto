package org.sanmibuh.cqrs.infrastructure;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sanmibuh.cqrs.port.BaseCommandHandler;
import org.sanmibuh.cqrs.port.QueryHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

@RequiredArgsConstructor(access = lombok.AccessLevel.PACKAGE)
class CQRSHandlerRegistrar implements BeanDefinitionRegistryPostProcessor {

  private static final AnnotationBeanNameGenerator NAME_GENERATOR =
      new AnnotationBeanNameGenerator();

  private final BeanFactory beanFactory;

  @Override
  public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry)
      throws BeansException {
    if (!AutoConfigurationPackages.has(beanFactory)) {
      return;
    }

    final var packages = AutoConfigurationPackages.get(beanFactory);
    registerHandlers(registry, packages, BaseCommandHandler.class);
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
        .forEach(candidate -> registerIfAbsent(registry, candidate));
  }

  private void registerIfAbsent(
      final BeanDefinitionRegistry registry, final BeanDefinition candidate) {
    findBeanClassNamePITEquivalent(candidate)
        .ifPresent(
            beanClassName -> {
              if (!isClassRegistered(registry, beanClassName)) {
                final var beanName = NAME_GENERATOR.generateBeanName(candidate, registry);
                registry.registerBeanDefinition(beanName, new RootBeanDefinition(beanClassName));
              }
            });
  }

  private boolean isClassRegistered(
      final BeanDefinitionRegistry registry, final String beanClassName) {
    return Arrays.stream(registry.getBeanDefinitionNames())
        .map(name -> registry.getBeanDefinition(name).getBeanClassName())
        .anyMatch(beanClassName::equals);
  }

  private Optional<String> findBeanClassNamePITEquivalent(final BeanDefinition candidate) {
    return Optional.ofNullable(candidate.getBeanClassName());
  }
}
