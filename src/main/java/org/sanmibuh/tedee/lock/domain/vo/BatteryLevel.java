package org.sanmibuh.tedee.lock.domain.vo;

import org.sanmibuh.tedee.lock.domain.exception.InvalidBatteryLevelException;

public record BatteryLevel(int value) {

  public BatteryLevel {
    if (value < 0 || value > 100) {
      throw new InvalidBatteryLevelException(value);
    }
  }
}
