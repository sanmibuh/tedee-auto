package org.sanmibuh.ddd.cqrs;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.sanmibuh.cqrs.port.Command;
import org.sanmibuh.ddd.domain.AggregateRoot;
import org.sanmibuh.ddd.domain.AggregateRootId;
import org.sanmibuh.ddd.domain.DomainEvent;

class AggregateCommandHandlerTest {

  @Test
  void should_returnAggregateWithEvents_whenProcessIsCalled() {
    final var sut = new StubAggregateCommandHandler();

    final var aggregate = sut.process(new StubCommand());

    then(aggregate.domainEvents()).containsExactly(new StubEvent());
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

  static class StubAggregateCommandHandler
      extends AggregateCommandHandler<StubCommand, StubAggregate> {

    @Override
    protected StubAggregate handle(final StubCommand command) {
      return new StubAggregate();
    }
  }
}
