package org.sanmibuh.framework.resilience;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RetryPropertiesTest {

  @ParameterizedTest
  @ValueSource(doubles = {0.5, 0.0})
  void should_rejectMultiplier_whenBelowOne(final double multiplier) {
    final var sut = new RetryProperties(2, 500, multiplier, 5000);

    then(sut.isMultiplierAtLeastOne()).isFalse();
  }

  @Test
  void should_rejectInitialInterval_whenExceedingMaximum() {
    final var sut = new RetryProperties(2, 5001, 2.0, 5000);

    then(sut.isInitialIntervalWithinMaxInterval()).isFalse();
  }
}
