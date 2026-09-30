package org.sanmibuh.tedee.lock.domain;

import org.sanmibuh.ddd.domain.DomainEvent;
import org.sanmibuh.ddd.domain.NoSubscribersRequired;

@NoSubscribersRequired
public record LockStateChanged(int deviceId, LockState state) implements DomainEvent {}
