package org.sanmibuh.tedee.lock.infrastructure.secondary;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "sanmibuh.rest.tedee")
public record TedeeProperties(@NotBlank String baseUrl, @NotBlank String apiKey) {}
