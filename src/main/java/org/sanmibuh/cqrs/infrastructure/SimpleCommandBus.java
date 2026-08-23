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
  public void dispatch(final Command command) {
    final CommandHandler<Command> handler = lookup.find(command.getClass());
    handler.handle(command);
  }
}
