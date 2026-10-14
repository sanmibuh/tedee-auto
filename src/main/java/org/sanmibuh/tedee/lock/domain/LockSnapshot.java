package org.sanmibuh.tedee.lock.domain;

import org.sanmibuh.ddd.domain.Snapshot;
import org.sanmibuh.tedee.lock.domain.vo.LockStatus;

public record LockSnapshot(int id, LockStatus status) implements Snapshot {}
