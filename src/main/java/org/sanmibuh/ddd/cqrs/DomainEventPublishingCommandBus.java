package org.sanmibuh.ddd.cqrs;

import java.util.List;
import org.sanmibuh.cqrs.port.BaseCommandHandler;
import org.sanmibuh.cqrs.port.Command;
import org.sanmibuh.cqrs.port.CommandBus;
import org.sanmibuh.ddd.port.EventBus;

public class DomainEventPublishingCommandBus implements CommandBus {

  public DomainEventPublishingCommandBus(
      final List<BaseCommandHandler<?, ?>> handlers, final EventBus eventBus) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void dispatch(final Command command) {
    throw new UnsupportedOperationException();
  }
}
