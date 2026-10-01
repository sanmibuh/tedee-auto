package org.sanmibuh.framework.resilience;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record RetryProperties(
    @PositiveOrZero int maxRetries,
    @Positive long initialInterval,
    @Positive double multiplier,
    @Positive long maxInterval) {

  @AssertTrue(message = "multiplier must be greater than or equal to 1")
  boolean isMultiplierAtLeastOne() {
    return multiplier >= 1;
  }

  @AssertTrue(message = "initialInterval must not exceed maxInterval")
  boolean isInitialIntervalWithinMaxInterval() {
    return initialInterval <= maxInterval;
  }
}
