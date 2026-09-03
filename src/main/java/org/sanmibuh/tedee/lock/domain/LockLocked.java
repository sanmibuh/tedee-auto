package org.sanmibuh.tedee.lock.domain;

import org.sanmibuh.ddd.domain.DomainEvent;
import org.sanmibuh.ddd.domain.NoSubscribersRequired;

@NoSubscribersRequired
public record LockLocked(int deviceId) implements DomainEvent {}
