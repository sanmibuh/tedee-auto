package org.sanmibuh.ddd.domain;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class AggregateRootTest {

  @InjectSoftAssertions private BDDSoftAssertions softly;

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

  @Test
  void should_beEqualIgnoringEvents_whenSameTypeAndSameId() {
    final var other = new TestAggregate(new TestId(42));
    other.emit(new TestEvent());

    softly.then(sut).isEqualTo(other);
    softly.then(sut).hasSameHashCodeAs(other);
    // Pins the exact hash value only to kill PIT's primitive-returns mutant (return 0) on hashCode;
    // the behavioural contract above is what actually matters.
    softly.then(sut.hashCode()).isEqualTo(new TestId(42).hashCode());
  }

  @Test
  void should_notBeEqual_whenSameTypeButDifferentId() {
    then(sut).isNotEqualTo(new TestAggregate(new TestId(7)));
  }

  @Test
  void should_notBeEqual_whenDifferentTypeButSameId() {
    then(sut).isNotEqualTo(new OtherAggregate(new TestId(42)));
  }

  private static final class TestAggregate extends AggregateRoot<TestId> {
    TestAggregate(final TestId id) {
      super(id);
    }

    void emit(final DomainEvent event) {
      recordEvent(event);
    }
  }

  private static final class OtherAggregate extends AggregateRoot<TestId> {
    OtherAggregate(final TestId id) {
      super(id);
    }
  }

  private record TestId(Integer value) implements AggregateRootId<Integer> {}

  private record TestEvent() implements DomainEvent {}
}
