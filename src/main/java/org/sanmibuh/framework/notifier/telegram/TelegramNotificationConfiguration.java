package org.sanmibuh.framework.notifier.telegram;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties(TelegramProperties.class)
public class TelegramNotificationConfiguration {}
