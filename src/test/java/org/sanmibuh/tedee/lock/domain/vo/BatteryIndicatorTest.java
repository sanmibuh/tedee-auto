package org.sanmibuh.tedee.lock.domain.vo;

import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class BatteryIndicatorTest {

  @InjectSoftAssertions BDDSoftAssertions softly;

  @Test
  void should_deriveLevelAndCategory_whenConstructedWithPercentage() {
    final var sut = new BatteryIndicator(42);

    softly.then(sut.getLevel()).isEqualTo(new BatteryLevel(42));
    softly.then(sut.getCategory()).isEqualTo(BatteryLevelCategory.MEDIUM);
  }
}
