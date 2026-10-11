package org.sanmibuh.tedee.lock.domain.event;

import org.sanmibuh.ddd.domain.DomainEvent;
import org.sanmibuh.tedee.lock.domain.vo.BatteryLevelCategory;

public record BatteryLevelReported(int deviceId, BatteryStatus battery) implements DomainEvent {

  public record BatteryStatus(int level, BatteryLevelCategory category) {}
}
