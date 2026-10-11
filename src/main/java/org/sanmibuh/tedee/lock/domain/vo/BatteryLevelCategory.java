package org.sanmibuh.tedee.lock.domain.vo;

public enum BatteryLevelCategory {
  DEPLETED,
  CRITICAL,
  LOW,
  MEDIUM,
  GOOD,
  HIGH;

  public static BatteryLevelCategory from(final int level) {
    if (level <= 10) {
      return DEPLETED;
    }
    if (level <= 20) {
      return CRITICAL;
    }
    if (level <= 40) {
      return LOW;
    }
    if (level <= 60) {
      return MEDIUM;
    }
    if (level <= 80) {
      return GOOD;
    }
    return HIGH;
  }
}
