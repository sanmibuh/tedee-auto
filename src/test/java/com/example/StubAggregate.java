package com.example;

import org.sanmibuh.ddd.domain.AggregateRoot;
import org.sanmibuh.ddd.domain.AggregateRootId;
import org.sanmibuh.ddd.domain.Snapshot;

public final class StubAggregate
    extends AggregateRoot<StubAggregate.StubId, StubAggregate.StubSnapshot> {

  public StubAggregate() {
    super(new StubId(1));
  }

  @Override
  public StubSnapshot snapshot() {
    return new StubSnapshot();
  }

  public record StubId(Integer value) implements AggregateRootId<Integer> {}

  public record StubSnapshot() implements Snapshot {}
}
