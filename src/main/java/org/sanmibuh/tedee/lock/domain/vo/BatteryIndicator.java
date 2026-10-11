package org.sanmibuh.tedee.lock.domain.vo;

public final class BatteryIndicator {

  private final BatteryLevel level = new BatteryLevel(0);
  private final BatteryLevelCategory category = BatteryLevelCategory.DEPLETED;

  public BatteryIndicator(final int level) {}

  public BatteryLevel level() {
    return level;
  }

  public BatteryLevelCategory category() {
    return category;
  }
}
