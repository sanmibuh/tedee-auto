package org.sanmibuh.ddd.port;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.sanmibuh.ddd.domain.AggregateRoot;
import org.sanmibuh.ddd.domain.AggregateRootId;
import org.sanmibuh.ddd.domain.DomainEvent;
import org.sanmibuh.ddd.domain.Snapshot;

class CommandHandlerTest {

  @Test
  void should_returnRecordedEvents_whenHandleIsCalled() {
    final var sut = new StubCommandHandler();

    final var events = sut.handle(new StubCommand());

    then(events).containsExactly(new StubEvent());
  }

  record StubCommand() implements Command {}

  record StubEvent() implements DomainEvent {}

  record StubId(Integer value) implements AggregateRootId<Integer> {}

  static final class StubAggregate extends AggregateRoot<StubId, StubSnapshot> {

    StubAggregate() {
      super(new StubId(1));
      recordEvent(new StubEvent());
    }

    @Override
    public StubSnapshot snapshot() {
      return new StubSnapshot();
    }
  }

  record StubSnapshot() implements Snapshot {}

  static class StubCommandHandler extends CommandHandler<StubCommand, StubAggregate> {

    @Override
    protected StubAggregate execute(final StubCommand command) {
      return new StubAggregate();
    }
  }
}
