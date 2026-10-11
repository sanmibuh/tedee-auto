package org.sanmibuh.tedee.lock.domain.vo;

import lombok.Getter;

@Getter
public final class BatteryIndicator {

  private final BatteryLevel level;
  private final BatteryLevelCategory category;

  public BatteryIndicator(final int level) {
    this.level = new BatteryLevel(level);
    category = BatteryLevelCategory.from(level);
  }
}
