package org.sanmibuh.ddd.cqrs.port;

import java.util.List;
import org.sanmibuh.cqrs.port.Command;
import org.sanmibuh.ddd.domain.AggregateRoot;
import org.sanmibuh.ddd.domain.DomainEvent;

public abstract class AggregateCommandHandler<C extends Command, A extends AggregateRoot<?>>
    implements DomainEventCommandHandler<C> {

  @Override
  public final List<DomainEvent> handle(final C command) {
    return execute(command).domainEvents();
  }

  protected abstract A execute(C command);
}
