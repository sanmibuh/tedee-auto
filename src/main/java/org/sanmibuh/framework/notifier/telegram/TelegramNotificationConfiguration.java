package org.sanmibuh.framework.notifier.telegram;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.resilience.annotation.EnableResilientMethods;

@AutoConfiguration
@EnableResilientMethods
@EnableConfigurationProperties(TelegramProperties.class)
public class TelegramNotificationConfiguration {}
