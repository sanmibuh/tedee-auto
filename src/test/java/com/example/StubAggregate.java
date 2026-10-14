package com.example;

import org.sanmibuh.ddd.domain.AggregateRoot;
import org.sanmibuh.ddd.domain.AggregateRootId;
import org.sanmibuh.ddd.domain.EntitySnapshot;

public final class StubAggregate
    extends AggregateRoot<StubAggregate.StubId, Integer, StubAggregate.StubSnapshot> {

  public StubAggregate() {
    super(new StubId(1));
  }

  @Override
  public StubSnapshot snapshot() {
    return new StubSnapshot(id().value());
  }

  public record StubId(Integer value) implements AggregateRootId<Integer> {}

  public record StubSnapshot(Integer id) implements EntitySnapshot<Integer> {}
}
