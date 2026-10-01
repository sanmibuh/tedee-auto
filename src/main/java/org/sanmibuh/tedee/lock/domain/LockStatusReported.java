package org.sanmibuh.tedee.lock.domain;

import org.sanmibuh.ddd.domain.DomainEvent;
import org.sanmibuh.ddd.domain.NoSubscribersRequired;

@NoSubscribersRequired
public record LockStatusReported(int deviceId, LockState state, LockJamStatus jamStatus)
    implements DomainEvent {}
