package org.sanmibuh.ddd.infrastructure;

import java.util.List;
import org.sanmibuh.ddd.domain.DomainEvent;
import org.sanmibuh.ddd.port.DomainEventHandler;
import org.sanmibuh.ddd.port.EventBus;

public final class InMemoryEventBus implements EventBus {

  private final EventHandlerRegistry registry;

  public InMemoryEventBus(final List<DomainEventHandler<?>> handlers) {
    registry = new EventHandlerRegistry(handlers);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void publish(final DomainEvent event) {
    for (final var handler : registry.handlersFor(event.getClass())) {
      ((DomainEventHandler<DomainEvent>) handler).handle(event);
    }
  }
}
