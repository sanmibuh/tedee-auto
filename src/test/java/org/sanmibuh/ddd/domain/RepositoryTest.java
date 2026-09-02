package org.sanmibuh.ddd.domain;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.sanmibuh.ddd.port.Repository;

class RepositoryTest {

  private final TestId id = new TestId(1);
  private final TestAggregate aggregate = new TestAggregate(id);

  @Test
  void should_returnAggregate_whenItExists() {
    final var sut = repositoryWith(aggregate);

    then(sut.get(id)).isSameAs(aggregate);
  }

  @Test
  void should_throwAggregateNotFoundException_whenAggregateIsAbsent() {
    final var sut = emptyRepository();

    thenThrownBy(() -> sut.get(id)).isInstanceOf(AggregateNotFoundException.class);
  }

  private Repository<TestAggregate, TestId> repositoryWith(final TestAggregate stored) {
    return new Repository<>() {
      @Override
      public Optional<TestAggregate> findById(final TestId id) {
        return Optional.of(stored);
      }

      @Override
      public void save(final TestAggregate aggregate) {
        // no op
      }
    };
  }

  private Repository<TestAggregate, TestId> emptyRepository() {
    return new Repository<>() {
      @Override
      public Optional<TestAggregate> findById(final TestId id) {
        return Optional.empty();
      }

      @Override
      public void save(final TestAggregate aggregate) {
        // no op
      }
    };
  }

  private static final class TestAggregate extends AggregateRoot<TestId> {

    TestAggregate(final TestId id) {
      super(id);
    }
  }

  private record TestId(Integer value) implements AggregateRootId<Integer> {}
}
