package org.sanmibuh.ddd.domain;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import java.util.Optional;
import org.jspecify.annotations.Nullable;
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

  @Test
  void should_saveSnapshot_whenSavingAggregate() {
    final var sut = new SnapshotCapturingRepository();

    sut.save(aggregate);

    then(sut.snapshot).isEqualTo(new TestSnapshot(id.value()));
  }

  private Repository<TestAggregate, TestId, Integer, TestSnapshot> repositoryWith(
      final TestAggregate stored) {
    return new Repository<>() {
      @Override
      public Optional<TestAggregate> findById(final TestId id) {
        return Optional.of(stored);
      }

      @Override
      public void save(final TestSnapshot snapshot) {
        // no op
      }
    };
  }

  private Repository<TestAggregate, TestId, Integer, TestSnapshot> emptyRepository() {
    return new Repository<>() {
      @Override
      public Optional<TestAggregate> findById(final TestId id) {
        return Optional.empty();
      }

      @Override
      public void save(final TestSnapshot snapshot) {
        // no op
      }
    };
  }

  private static final class TestAggregate extends AggregateRoot<TestId, Integer, TestSnapshot> {

    TestAggregate(final TestId id) {
      super(id);
    }

    @Override
    public TestSnapshot snapshot() {
      return new TestSnapshot(id().value());
    }
  }

  private record TestSnapshot(Integer id) implements EntitySnapshot<Integer> {}

  private static final class SnapshotCapturingRepository
      implements Repository<TestAggregate, TestId, Integer, TestSnapshot> {

    private @Nullable TestSnapshot snapshot;

    @Override
    public Optional<TestAggregate> findById(final TestId id) {
      return Optional.empty();
    }

    @Override
    public void save(final TestSnapshot snapshot) {
      this.snapshot = snapshot;
    }
  }

  private record TestId(Integer value) implements AggregateRootId<Integer> {}
}
