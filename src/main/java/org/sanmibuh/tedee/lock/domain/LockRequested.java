package org.sanmibuh.tedee.lock.domain;

import org.sanmibuh.ddd.domain.DomainEvent;

public record LockRequested(int deviceId) implements DomainEvent {}
