package org.sanmibuh.tedee.lock.domain.vo;

import org.sanmibuh.ddd.domain.AggregateRootId;
import org.sanmibuh.tedee.lock.domain.exception.InvalidLockIdException;

public record LockId(Integer value) implements AggregateRootId<Integer> {

  public LockId {
    if (value <= 0) {
      throw new InvalidLockIdException(value);
    }
  }
}
