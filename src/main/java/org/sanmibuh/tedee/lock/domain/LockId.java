package org.sanmibuh.tedee.lock.domain;

import org.sanmibuh.ddd.domain.AggregateRootId;

public record LockId(Integer value) implements AggregateRootId<Integer> {

  public LockId {
    if (value <= 0) {
      throw new InvalidLockIdException(value);
    }
  }
}
