package org.sanmibuh.cqrs.infrastructure;

import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.sanmibuh.cqrs.port.Command;
import org.sanmibuh.cqrs.port.CommandHandler;
import org.sanmibuh.cqrs.port.HandlerNotFoundException;
import org.sanmibuh.ddd.domain.AggregateRoot;
import org.sanmibuh.ddd.domain.AggregateRootId;
import org.sanmibuh.ddd.domain.DomainEvent;
import org.sanmibuh.ddd.port.EventBus;

class InMemoryCommandBusTest {

  private final EventBus eventBus = mock(EventBus.class);

  @Test
  void should_publishRecordedEvents_whenHandlerIsRegistered() {
    final var bus = new InMemoryCommandBus(List.of(new StubCommandHandler()), eventBus);

    bus.dispatch(new StubCommand());

    verify(eventBus).publish(new StubEvent());
  }

  @Test
  void should_throwHandlerNotFoundException_whenNoHandlerRegistered() {
    final var bus = new InMemoryCommandBus(List.of(), eventBus);

    thenThrownBy(() -> bus.dispatch(new StubCommand()))
        .isInstanceOf(HandlerNotFoundException.class)
        .hasMessageContaining(StubCommand.class.getName());
  }

  record StubCommand() implements Command {}

  record StubEvent() implements DomainEvent {}

  record StubId(Integer value) implements AggregateRootId<Integer> {}

  static final class StubAggregate extends AggregateRoot<StubId> {

    StubAggregate() {
      super(new StubId(1));
      recordEvent(new StubEvent());
    }
  }

  static class StubCommandHandler extends CommandHandler<StubCommand, StubAggregate> {

    @Override
    protected StubAggregate execute(final StubCommand command) {
      return new StubAggregate();
    }
  }
}
