package org.sanmibuh.tedee;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sanmibuh.server")
public record ServerProperties(String publicUrl) {}
