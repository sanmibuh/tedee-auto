package org.sanmibuh.tedee.lock.domain.event;

import org.sanmibuh.ddd.domain.DomainEvent;

public record LockDisconnected(int deviceId, String name) implements DomainEvent {}
