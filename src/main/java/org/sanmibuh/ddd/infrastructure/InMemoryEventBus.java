package org.sanmibuh.ddd.infrastructure;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.sanmibuh.ddd.domain.DomainEvent;
import org.sanmibuh.ddd.domain.NoSubscribersRequired;
import org.sanmibuh.ddd.port.DomainEventHandler;
import org.sanmibuh.ddd.port.EventBus;

@Slf4j
public final class InMemoryEventBus implements EventBus {

  private final EventHandlerRegistry registry;

  public InMemoryEventBus(final List<DomainEventHandler<?>> handlers) {
    registry = new EventHandlerRegistry(handlers);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void publish(final DomainEvent event) {
    final var handlers = registry.handlersFor(event.getClass());
    if (isDropped(event, handlers)) {
      log.warn(
          "No handler registered for domain event {}; the event was dropped and not delivered.",
          event.getClass().getName());
      return;
    }
    for (final var handler : handlers) {
      ((DomainEventHandler<DomainEvent>) handler).handle(event);
    }
  }

  private static boolean isDropped(
      final DomainEvent event, final List<DomainEventHandler<?>> handlers) {
    return handlers.isEmpty() && !event.getClass().isAnnotationPresent(NoSubscribersRequired.class);
  }
}
