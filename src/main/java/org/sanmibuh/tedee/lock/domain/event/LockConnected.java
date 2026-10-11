package org.sanmibuh.tedee.lock.domain.event;

import org.sanmibuh.ddd.domain.DomainEvent;

public record LockConnected(int deviceId) implements DomainEvent {}
