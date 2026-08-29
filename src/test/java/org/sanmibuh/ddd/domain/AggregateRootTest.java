package org.sanmibuh.ddd.domain;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import org.junit.jupiter.api.Test;

class AggregateRootTest {

  private final TestAggregate sut = new TestAggregate();

  @Test
  void should_exposeRecordedEvents_whenEventsAreRecordedInOrder() {
    final var first = new TestEvent();
    final var second = new TestEvent();

    sut.emit(first);
    sut.emit(second);

    then(sut.domainEvents()).containsExactly(first, second);
  }

  @Test
  void should_returnUnmodifiableEvents_whenExposingDomainEvents() {
    thenThrownBy(() -> sut.domainEvents().add(new TestEvent()))
        .isInstanceOf(UnsupportedOperationException.class);
  }

  private static final class TestAggregate extends AggregateRoot {
    void emit(final DomainEvent event) {
      recordEvent(event);
    }
  }

  private record TestEvent() implements DomainEvent {}
}
