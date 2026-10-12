package org.sanmibuh.tedee.lock.infrastructure;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.Duration;
import org.sanmibuh.framework.resilience.RetryProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "sanmibuh.rest.tedee")
public record TedeeProperties(
    @NotNull URI baseUrl,
    @NotBlank String apiKey,
    @NotBlank String callbackSecret,
    @NotNull Duration callbackRegistrationInterval,
    @Valid @NotNull RetryProperties retry) {

  @AssertTrue(message = "callbackRegistrationInterval must be positive")
  boolean isCallbackRegistrationIntervalPositive() {
    return !callbackRegistrationInterval.isNegative() && !callbackRegistrationInterval.isZero();
  }

  @AssertTrue(message = "baseUrl must be an absolute URL with a host")
  boolean isBaseUrlValid() {
    return baseUrl.isAbsolute() && baseUrl.getHost() != null;
  }
}
