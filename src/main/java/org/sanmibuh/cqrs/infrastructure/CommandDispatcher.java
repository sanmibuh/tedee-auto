package org.sanmibuh.cqrs.infrastructure;

import java.util.List;
import org.jspecify.annotations.Nullable;
import org.sanmibuh.cqrs.port.BaseCommandHandler;
import org.sanmibuh.cqrs.port.Command;

public final class CommandDispatcher {

  private final HandlerLookup<BaseCommandHandler<?, ?>> lookup;

  public CommandDispatcher(final List<BaseCommandHandler<?, ?>> handlers) {
    lookup = new HandlerLookup<>(handlers, BaseCommandHandler.class);
  }

  @SuppressWarnings("unchecked")
  public @Nullable Object dispatch(final Command command) {
    final var handler = (BaseCommandHandler<Command, ?>) lookup.find(command.getClass());
    return handler.handle(command);
  }
}
