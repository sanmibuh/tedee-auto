package org.sanmibuh.tedee.lock.domain;

import org.sanmibuh.ddd.domain.EntitySnapshot;
import org.sanmibuh.tedee.lock.domain.vo.LockStatus;

public record LockSnapshot(Integer id, LockStatus status) implements EntitySnapshot<Integer> {}
