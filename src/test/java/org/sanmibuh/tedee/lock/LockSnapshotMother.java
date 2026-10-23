package org.sanmibuh.tedee.lock;

import java.util.List;
import lombok.experimental.UtilityClass;
import org.sanmibuh.tedee.lock.domain.LockSnapshot;
import org.sanmibuh.tedee.lock.domain.vo.LockStatus;

@UtilityClass
public final class LockSnapshotMother {

  public static final int ID = 1;
  public static final String NAME = "Front door";

  public static LockSnapshot snapshot(final LockStatus status, final String... cronExpressions) {
    return new LockSnapshot(ID, NAME, status, List.of(cronExpressions));
  }
}
