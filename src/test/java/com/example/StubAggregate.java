package com.example;

import org.sanmibuh.ddd.domain.AggregateRoot;
import org.sanmibuh.ddd.domain.AggregateRootId;

public final class StubAggregate extends AggregateRoot<StubAggregate.StubId> {

  public StubAggregate() {
    super(new StubId(1));
  }

  public record StubId(Integer value) implements AggregateRootId<Integer> {}
}
