package org.sanmibuh.tedee.lock.infrastructure;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.sanmibuh.framework.resilience.RetryProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "sanmibuh.rest.tedee")
public record TedeeProperties(
    @NotBlank String baseUrl,
    @NotBlank String apiKey,
    @NotBlank String callbackSecret,
    @NotNull Duration callbackRegistrationInterval,
    @Valid @NotNull RetryProperties retry) {}
