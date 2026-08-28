package org.sanmibuh.tedee.lock.domain;

import org.sanmibuh.ddd.domain.DomainEvent;

public record LockLocked(LockId lockId) implements DomainEvent {}
