package org.sanmibuh.tedee.lock.infrastructure.primary;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "sanmibuh.scheduler.lock")
public record LockSchedulerProperties(@DefaultValue Map<Integer, String> schedules) {}
