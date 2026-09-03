package org.sanmibuh.tedee.automation;

import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.assertj.core.api.BDDAssertions.then;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.sanmibuh.ddd.domain.DomainEvent;
import org.sanmibuh.ddd.domain.NoSubscribersRequired;
import org.sanmibuh.ddd.port.DomainEventHandler;
import org.springframework.core.GenericTypeResolver;

class DomainEventSubscriptionTest {

  private final JavaClasses classes =
      new ClassFileImporter()
          .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
          .importPackages("org.sanmibuh");

  @Test
  void should_haveHandlerOrOptOut_forEveryDomainEvent() {
    final var handledEventTypes = handledEventTypes();

    final var uncovered =
        concreteImplementationsOf(DomainEvent.class)
            .map(JavaClass::reflect)
            .filter(event -> !event.isAnnotationPresent(NoSubscribersRequired.class))
            .filter(event -> !handledEventTypes.contains(event))
            .map(Class::getName)
            .toList();

    then(uncovered).isEmpty();
  }

  private Set<Class<?>> handledEventTypes() {
    return concreteImplementationsOf(DomainEventHandler.class)
        .map(JavaClass::reflect)
        .map(this::resolveEventType)
        .collect(toUnmodifiableSet());
  }

  private Class<?> resolveEventType(final Class<?> handler) {
    final var typeArgs =
        GenericTypeResolver.resolveTypeArguments(handler, DomainEventHandler.class);
    return Objects.requireNonNull(typeArgs, "Cannot resolve event type for " + handler.getName())[
        0];
  }

  private Stream<JavaClass> concreteImplementationsOf(final Class<?> type) {
    return classes.stream()
        .filter(candidate -> candidate.isAssignableTo(type))
        .filter(candidate -> !candidate.isInterface())
        .filter(candidate -> !candidate.getModifiers().contains(JavaModifier.ABSTRACT));
  }
}
