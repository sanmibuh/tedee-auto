package org.sanmibuh.cqrs.infrastructure;

import java.util.List;
import org.sanmibuh.cqrs.port.BaseCommandHandler;
import org.sanmibuh.cqrs.port.Command;
import org.sanmibuh.cqrs.port.CommandBus;

public final class InMemoryCommandBus implements CommandBus {

  private final HandlerLookup<BaseCommandHandler<?, ?>> lookup;

  public InMemoryCommandBus(final List<BaseCommandHandler<?, ?>> handlers) {
    lookup = new HandlerLookup<>(handlers, BaseCommandHandler.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void dispatch(final Command command) {
    final var handler = (BaseCommandHandler<Command, ?>) lookup.find(command.getClass());
    handler.handle(command);
  }
}
