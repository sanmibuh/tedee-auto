package org.sanmibuh.ddd.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.sanmibuh.ddd.domain.DomainEvent;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.ClassUtils;

final class DomainEventScanner {

  Set<Class<?>> scan(final List<String> packages) {
    final var scanner = new ClassPathScanningCandidateComponentProvider(false);
    scanner.addIncludeFilter(new AssignableTypeFilter(DomainEvent.class));
    return packages.stream()
        .flatMap(basePackage -> scanner.findCandidateComponents(basePackage).stream())
        .map(BeanDefinition::getBeanClassName)
        .map(Optional::ofNullable)
        .flatMap(Optional::stream)
        .map(this::loadClass)
        .collect(Collectors.toUnmodifiableSet());
  }

  private Class<?> loadClass(final String className) {
    return ClassUtils.resolveClassName(className, ClassUtils.getDefaultClassLoader());
  }
}
