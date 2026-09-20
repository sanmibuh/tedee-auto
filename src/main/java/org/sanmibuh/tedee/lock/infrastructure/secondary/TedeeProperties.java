package org.sanmibuh.tedee.lock.infrastructure.secondary;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "sanmibuh.rest.tedee")
public record TedeeProperties(
    @NotBlank String baseUrl, @NotBlank String apiKey, @Valid @NotNull Retry retry) {

  public record Retry(
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
}
