package com.example;

import org.sanmibuh.ddd.domain.DomainEvent;
import org.sanmibuh.ddd.port.DomainEventHandler;

public class StubEventHandler implements DomainEventHandler<StubEventHandler.StubEvent> {

  public boolean handled;

  @Override
  public void handle(final StubEvent event) {
    handled = true;
  }

  public record StubEvent() implements DomainEvent {}
}
