package org.sanmibuh.tedee.lock.domain.event;

import org.sanmibuh.ddd.domain.DomainEvent;

public record LockRequested(int deviceId, String name) implements DomainEvent {}
