package org.sanmibuh.tedee;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "sanmibuh.server")
public record ServerProperties(@NotBlank String publicUrl) {}
