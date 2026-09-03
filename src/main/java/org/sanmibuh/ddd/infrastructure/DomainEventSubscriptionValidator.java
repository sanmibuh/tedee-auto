package org.sanmibuh.ddd.infrastructure;

import java.util.Collection;
import java.util.Set;
import org.sanmibuh.ddd.domain.NoSubscribersRequired;

final class DomainEventSubscriptionValidator {

  void validate(
      final Collection<Class<?>> publishableEvents, final Set<Class<?>> handledEventTypes) {
    for (final var eventType : publishableEvents) {
      if (!handledEventTypes.contains(eventType)
          && !eventType.isAnnotationPresent(NoSubscribersRequired.class)) {
        throw new MissingEventHandlerException(eventType);
      }
    }
  }
}
