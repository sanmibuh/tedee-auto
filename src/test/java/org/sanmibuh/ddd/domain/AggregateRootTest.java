package org.sanmibuh.ddd.domain;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import org.junit.jupiter.api.Test;

class AggregateRootTest {

  private final TestAggregate sut = new TestAggregate(new TestId(42));

  @Test
  void should_exposeItsId_whenConstructedWithAnId() {
    then(sut.id()).isEqualTo(new TestId(42));
  }

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

  private static final class TestAggregate extends AggregateRoot<TestId> {
    TestAggregate(final TestId id) {
      super(id);
    }

    void emit(final DomainEvent event) {
      recordEvent(event);
    }
  }

  private record TestId(Integer value) implements AggregateRootId<Integer> {}

  private record TestEvent() implements DomainEvent {}
}
