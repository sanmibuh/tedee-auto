package org.sanmibuh.ddd.infrastructure;

import java.util.Collection;
import java.util.Set;
import org.sanmibuh.ddd.domain.NoSubscribersRequired;

final class DomainEventSubscriptionValidator {

  void validate(
      final Collection<Class<?>> publishableEvents, final Set<Class<?>> handledEventTypes) {
    publishableEvents.stream()
        .filter(eventType -> !handledEventTypes.contains(eventType))
        .filter(eventType -> !eventType.isAnnotationPresent(NoSubscribersRequired.class))
        .findFirst()
        .ifPresent(
            eventType -> {
              throw new MissingEventHandlerException(eventType);
            });
  }
}
