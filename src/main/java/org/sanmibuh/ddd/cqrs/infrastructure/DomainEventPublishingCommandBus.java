package org.sanmibuh.ddd.cqrs.infrastructure;

import java.util.List;
import org.sanmibuh.cqrs.infrastructure.CommandDispatcher;
import org.sanmibuh.cqrs.port.BaseCommandHandler;
import org.sanmibuh.cqrs.port.Command;
import org.sanmibuh.cqrs.port.CommandBus;
import org.sanmibuh.ddd.domain.AggregateRoot;
import org.sanmibuh.ddd.port.EventBus;

public final class DomainEventPublishingCommandBus implements CommandBus {

  private final CommandDispatcher dispatcher;
  private final EventBus eventBus;

  public DomainEventPublishingCommandBus(
      final List<BaseCommandHandler<?, ?>> handlers, final EventBus eventBus) {
    dispatcher = new CommandDispatcher(handlers);
    this.eventBus = eventBus;
  }

  @Override
  public void dispatch(final Command command) {
    final var result = dispatcher.dispatch(command);
    if (result instanceof final AggregateRoot<?> aggregate) {
      aggregate.domainEvents().forEach(eventBus::publish);
    }
  }
}
