package org.sanmibuh.ddd.port;

import org.sanmibuh.ddd.domain.DomainEvent;

public interface EventBus {

  void publish(DomainEvent event);
}
