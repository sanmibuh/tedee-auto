package org.sanmibuh.tedee.lock.domain.vo;

import java.util.Arrays;

public enum BatteryLevelCategory {
  DEPLETED(0, 10),
  CRITICAL(11, 20),
  LOW(21, 40),
  MEDIUM(41, 60),
  GOOD(61, 80),
  HIGH(81, 100);

  private final int minimum;
  private final int maximum;

  BatteryLevelCategory(final int minimum, final int maximum) {
    this.minimum = minimum;
    this.maximum = maximum;
  }

  public static BatteryLevelCategory from(final BatteryLevel level) {
    return Arrays.stream(values())
        .filter(category -> category.includes(level.value()))
        .findFirst()
        .orElseThrow();
  }

  private boolean includes(final int level) {
    return level >= minimum && level <= maximum;
  }
}
