package org.sanmibuh.cqrs.infrastructure;

import java.util.List;
import org.sanmibuh.cqrs.port.BaseCommandHandler;
import org.sanmibuh.cqrs.port.Command;
import org.sanmibuh.cqrs.port.CommandBus;

public final class InMemoryCommandBus implements CommandBus {

  private final CommandDispatcher dispatcher;

  public InMemoryCommandBus(final List<BaseCommandHandler<?, ?>> handlers) {
    dispatcher = new CommandDispatcher(handlers);
  }

  @Override
  public void dispatch(final Command command) {
    dispatcher.dispatch(command);
  }
}
