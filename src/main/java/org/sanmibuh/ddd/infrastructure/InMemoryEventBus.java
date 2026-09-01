package org.sanmibuh.ddd.infrastructure;

import java.util.List;
import org.sanmibuh.ddd.domain.DomainEvent;
import org.sanmibuh.ddd.port.DomainEventHandler;
import org.sanmibuh.ddd.port.EventBus;

public class InMemoryEventBus implements EventBus {

  public InMemoryEventBus(final List<DomainEventHandler<?>> handlers) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void publish(final DomainEvent event) {
    throw new UnsupportedOperationException();
  }
}
