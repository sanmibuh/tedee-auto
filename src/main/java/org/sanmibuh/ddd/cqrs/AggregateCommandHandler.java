package org.sanmibuh.ddd.cqrs;

import org.sanmibuh.cqrs.port.BaseCommandHandler;
import org.sanmibuh.cqrs.port.Command;
import org.sanmibuh.ddd.domain.AggregateRoot;

public abstract class AggregateCommandHandler<C extends Command, A extends AggregateRoot<?>>
    implements BaseCommandHandler<C, A> {

  @Override
  public final A process(final C command) {
    throw new UnsupportedOperationException();
  }

  protected abstract A handle(C command);
}
