package org.sanmibuh.ddd.domain;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.sanmibuh.ddd.port.Repository;

class RepositoryTest {

  private final TestId id = new TestId(1);
  private final TestAggregate aggregate = new TestAggregate(id);

  private static Repository<TestAggregate, TestId> repositoryReturning(
      final Optional<TestAggregate> result) {
    return new Repository<>() {
      @Override
      public Optional<TestAggregate> findById(final TestId id) {
        return result;
      }

      @Override
      public void save(final TestAggregate aggregate) {
        // no-op
      }
    };
  }

  @Test
  void should_returnAggregate_whenItExists() {
    final var sut = repositoryReturning(Optional.of(aggregate));

    then(sut.get(id)).isSameAs(aggregate);
  }

  @Test
  void should_throwAggregateNotFoundException_whenAggregateIsAbsent() {
    final var sut = repositoryReturning(Optional.empty());

    thenThrownBy(() -> sut.get(id)).isInstanceOf(AggregateNotFoundException.class);
  }

  private static final class TestAggregate extends AggregateRoot<TestId> {
    TestAggregate(final TestId id) {
      super(id);
    }
  }

  private record TestId(Integer value) implements AggregateRootId<Integer> {}
}
