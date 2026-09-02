package org.sanmibuh.ddd.port;

import java.util.List;
import org.sanmibuh.ddd.domain.AggregateRoot;
import org.sanmibuh.ddd.domain.DomainEvent;

public abstract class CommandHandler<C extends Command, A extends AggregateRoot<?>> {

  protected abstract A execute(C command);

  public final List<DomainEvent> handle(final C command) {
    return execute(command).domainEvents();
  }
}
