package org.sanmibuh.tedee.lock.infrastructure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TedeeProperties.class)
public class TedeeInfrastructureConfiguration {

  public static final String EVENTS_PATH = "/tedee/events";
  public static final String CALLBACK_SECRET_HEADER = "X-Tedee-Callback-Secret";
}
