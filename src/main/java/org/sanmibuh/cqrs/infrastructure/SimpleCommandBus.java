package org.sanmibuh.cqrs.infrastructure;

import java.util.List;
import org.sanmibuh.cqrs.domain.Command;
import org.sanmibuh.cqrs.domain.CommandBus;
import org.sanmibuh.cqrs.domain.CommandHandler;

public class SimpleCommandBus implements CommandBus {

  private final HandlerLookup<CommandHandler<?>> lookup;

  public SimpleCommandBus(final List<CommandHandler<?>> handlers) {
    lookup = new HandlerLookup<>(handlers, CommandHandler.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void dispatch(final Command command) {
    final var handler = (CommandHandler<Command>) lookup.find(command.getClass());
    handler.handle(command);
  }
}
