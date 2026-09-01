package org.sanmibuh.ddd.cqrs;

import org.sanmibuh.cqrs.port.BaseCommandHandler;
import org.sanmibuh.cqrs.port.Command;
import org.sanmibuh.ddd.domain.AggregateRoot;

public abstract class AggregateCommandHandler<C extends Command, A extends AggregateRoot<?>>
    implements BaseCommandHandler<C, A> {

  @Override
  public final A handle(final C command) {
    return execute(command);
  }

  protected abstract A execute(C command);
}
