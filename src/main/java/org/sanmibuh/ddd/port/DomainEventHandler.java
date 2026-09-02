package org.sanmibuh.ddd.port;

import org.sanmibuh.ddd.domain.DomainEvent;

public interface DomainEventHandler<E extends DomainEvent> {

  void handle(E event);
}
