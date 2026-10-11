package org.sanmibuh.tedee.lock.domain.vo;

import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sanmibuh.tedee.lock.domain.exception.InvalidBatteryLevelException;

class BatteryLevelTest {

  @ParameterizedTest
  @ValueSource(ints = {-1, 101})
  void should_throwException_whenValueIsOutsidePercentageRange(final int value) {
    thenThrownBy(() -> new BatteryLevel(value)).isInstanceOf(InvalidBatteryLevelException.class);
  }
}
