package org.sanmibuh.ddd.infrastructure;

import java.util.List;
import org.sanmibuh.ddd.port.Command;
import org.sanmibuh.ddd.port.CommandBus;
import org.sanmibuh.ddd.port.CommandHandler;
import org.sanmibuh.ddd.port.EventBus;

public final class InMemoryCommandBus implements CommandBus {

  private final HandlerLookup<CommandHandler<?, ?>> lookup;
  private final EventBus eventBus;

  public InMemoryCommandBus(final List<CommandHandler<?, ?>> handlers, final EventBus eventBus) {
    lookup = new HandlerLookup<>(handlers, CommandHandler.class);
    this.eventBus = eventBus;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void dispatch(final Command command) {
    final var handler = (CommandHandler<Command, ?>) lookup.find(command.getClass());
    handler.handle(command).forEach(eventBus::publish);
  }
}
