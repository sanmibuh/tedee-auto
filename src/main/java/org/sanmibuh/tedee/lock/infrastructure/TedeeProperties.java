package org.sanmibuh.tedee.lock.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sanmibuh.rest.tedee")
public record TedeeProperties(String baseUrl) {}
