package org.sanmibuh.ddd.infrastructure;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.STRING;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.sanmibuh.ddd.domain.AggregateRoot;
import org.sanmibuh.ddd.domain.AggregateRootId;
import org.sanmibuh.ddd.domain.DomainEvent;
import org.sanmibuh.ddd.port.Command;
import org.sanmibuh.ddd.port.CommandHandler;
import org.sanmibuh.ddd.port.EventBus;

class InMemoryCommandBusTest {

  private final EventBus eventBus = mock(EventBus.class);

  @Test
  void should_publishRecordedEvents_whenHandlerIsRegistered() {
    final var sut = new InMemoryCommandBus(List.of(new StubCommandHandler()), eventBus);

    sut.dispatch(new StubCommand(42));

    verify(eventBus).publish(new StubEvent());
  }

  @Test
  void should_throwHandlerNotFoundException_whenNoHandlerRegistered() {
    final var sut = new InMemoryCommandBus(List.of(), eventBus);

    thenThrownBy(() -> sut.dispatch(new StubCommand(42)))
        .isInstanceOf(HandlerNotFoundException.class)
        .hasMessageContaining(StubCommand.class.getName());
  }

  @Test
  void should_logDispatchedCommand_whenHandlerIsRegistered() {
    final var command = new StubCommand(42);
    final var sut = new InMemoryCommandBus(List.of(new StubCommandHandler()), eventBus);

    try (final var logCaptor = LogCaptor.forClass(InMemoryCommandBus.class)) {
      sut.dispatch(command);

      then(logCaptor.getInfoLogs()).singleElement(STRING).contains(command.toString());
    }
  }

  record StubCommand(int deviceId) implements Command {}

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
