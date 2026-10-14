package org.sanmibuh.tedee.lock.adapter.primary;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LockSchedulerProperties.class)
public class LockSchedulingConfiguration {}
