package org.sanmibuh.tedee.lock.infrastructure.primary;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sanmibuh.scheduler.lock")
public record LockSchedulerProperties(Map<Integer, String> schedules) {}
