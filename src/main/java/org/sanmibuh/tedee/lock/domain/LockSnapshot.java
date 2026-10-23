package org.sanmibuh.tedee.lock.domain;

import java.util.List;
import org.sanmibuh.ddd.domain.EntitySnapshot;
import org.sanmibuh.tedee.lock.domain.vo.LockState;

public record LockSnapshot(Integer id, String name, LockState state, List<String> cronExpressions)
    implements EntitySnapshot<Integer> {

  public LockSnapshot {
    cronExpressions = List.copyOf(cronExpressions);
  }
}
