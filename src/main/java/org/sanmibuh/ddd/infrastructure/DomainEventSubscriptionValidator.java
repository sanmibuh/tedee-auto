package org.sanmibuh.ddd.infrastructure;

import java.util.Collection;
import java.util.Set;

final class DomainEventSubscriptionValidator {

  void validate(
      final Collection<Class<?>> publishableEvents, final Set<Class<?>> handledEventTypes) {
    for (final var eventType : publishableEvents) {
      if (!handledEventTypes.contains(eventType)) {
        throw new MissingEventHandlerException(eventType);
      }
    }
  }
}
