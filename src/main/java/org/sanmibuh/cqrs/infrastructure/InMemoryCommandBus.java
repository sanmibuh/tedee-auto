package org.sanmibuh.cqrs.infrastructure;

import java.util.List;
import org.sanmibuh.cqrs.port.Command;
import org.sanmibuh.cqrs.port.CommandBus;
import org.sanmibuh.cqrs.port.CommandHandler;

public class InMemoryCommandBus implements CommandBus {

  private final HandlerLookup<CommandHandler<?>> lookup;

  public InMemoryCommandBus(final List<CommandHandler<?>> handlers) {
    lookup = new HandlerLookup<>(handlers, CommandHandler.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void dispatch(final Command command) {
    final var handler = (CommandHandler<Command>) lookup.find(command.getClass());
    handler.handle(command);
  }
}
