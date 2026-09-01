package org.sanmibuh.ddd.cqrs;

import java.util.List;
import org.sanmibuh.cqrs.port.BaseCommandHandler;
import org.sanmibuh.cqrs.port.Command;
import org.sanmibuh.cqrs.port.CommandBus;
import org.sanmibuh.ddd.domain.AggregateRoot;
import org.sanmibuh.ddd.port.EventBus;

public class DomainEventPublishingCommandBus implements CommandBus {

  private final HandlerLookup lookup;
  private final EventBus eventBus;

  public DomainEventPublishingCommandBus(
      final List<BaseCommandHandler<?, ?>> handlers, final EventBus eventBus) {
    this.lookup = new HandlerLookup(handlers);
    this.eventBus = eventBus;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void dispatch(final Command command) {
    final var handler = (BaseCommandHandler<Command, ?>) lookup.find(command.getClass());
    final var result = handler.handle(command);
    if (result instanceof final AggregateRoot<?> aggregate) {
      aggregate.domainEvents().forEach(eventBus::publish);
    }
  }
}
