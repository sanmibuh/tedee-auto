package org.sanmibuh.tedee.lock.domain.event;

import org.sanmibuh.ddd.domain.DomainEvent;

public record BatteryLevelReported(int deviceId, int level) implements DomainEvent {}
