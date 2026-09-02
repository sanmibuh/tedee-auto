package org.sanmibuh.ddd.cqrs.infrastructure;

import java.util.List;
import org.sanmibuh.cqrs.infrastructure.HandlerLookup;
import org.sanmibuh.cqrs.port.Command;
import org.sanmibuh.cqrs.port.CommandBus;
import org.sanmibuh.ddd.cqrs.port.DomainEventCommandHandler;
import org.sanmibuh.ddd.port.EventBus;

public final class DomainEventPublishingCommandBus implements CommandBus {

  private final HandlerLookup<DomainEventCommandHandler<?>> lookup;
  private final EventBus eventBus;

  public DomainEventPublishingCommandBus(
      final List<DomainEventCommandHandler<?>> handlers, final EventBus eventBus) {
    lookup = new HandlerLookup<>(handlers, DomainEventCommandHandler.class);

    this.eventBus = eventBus;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void dispatch(final Command command) {
    final var handler = (DomainEventCommandHandler<Command>) lookup.find(command.getClass());
    handler.handle(command).forEach(eventBus::publish);
  }
}
