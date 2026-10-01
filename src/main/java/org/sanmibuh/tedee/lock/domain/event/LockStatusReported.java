package org.sanmibuh.tedee.lock.domain.event;

import org.sanmibuh.ddd.domain.DomainEvent;
import org.sanmibuh.ddd.domain.NoSubscribersRequired;
import org.sanmibuh.tedee.lock.domain.vo.LockJamStatus;
import org.sanmibuh.tedee.lock.domain.vo.LockState;

@NoSubscribersRequired
public record LockStatusReported(int deviceId, LockState state, LockJamStatus jamStatus)
    implements DomainEvent {}
