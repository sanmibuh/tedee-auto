package org.sanmibuh.ddd.cqrs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.sanmibuh.cqrs.port.Command;
import org.sanmibuh.ddd.domain.AggregateRoot;
import org.sanmibuh.ddd.domain.AggregateRootId;
import org.sanmibuh.ddd.domain.DomainEvent;
import org.sanmibuh.ddd.port.EventBus;

class DomainEventPublishingCommandBusTest {

  @Test
  void should_publishDomainEvents_whenHandlerReturnsAggregate() {
    final var eventBus = mock(EventBus.class);
    final var sut =
        new DomainEventPublishingCommandBus(List.of(new StubAggregateHandler()), eventBus);

    sut.dispatch(new StubCommand());

    verify(eventBus).publish(new StubEvent());
  }

  record StubCommand() implements Command {}

  record StubEvent() implements DomainEvent {}

  record StubId(Integer value) implements AggregateRootId<Integer> {}

  static class StubAggregate extends AggregateRoot<StubId> {

    StubAggregate() {
      super(new StubId(1));
      recordEvent(new StubEvent());
    }
  }

  static class StubAggregateHandler extends AggregateCommandHandler<StubCommand, StubAggregate> {

    @Override
    protected StubAggregate execute(final StubCommand command) {
      return new StubAggregate();
    }
  }
}
