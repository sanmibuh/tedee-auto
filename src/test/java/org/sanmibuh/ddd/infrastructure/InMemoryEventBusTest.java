package org.sanmibuh.ddd.infrastructure;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.sanmibuh.ddd.domain.DomainEvent;
import org.sanmibuh.ddd.port.DomainEventHandler;

class InMemoryEventBusTest {

  @Test
  void should_deliverEvent_whenSingleHandlerIsRegistered() {
    final var handler = new StubEventHandler();
    final var sut = new InMemoryEventBus(List.of(handler));

    sut.publish(new StubEvent());

    then(handler.handled).isTrue();
  }

  record StubEvent() implements DomainEvent {}

  static class StubEventHandler implements DomainEventHandler<StubEvent> {

    boolean handled;

    @Override
    public void handle(final StubEvent event) {
      handled = true;
    }
  }
}
