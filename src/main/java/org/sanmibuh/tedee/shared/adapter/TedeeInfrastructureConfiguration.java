package org.sanmibuh.tedee.shared.adapter;

import org.sanmibuh.tedee.ApplicationLocale;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({TedeeProperties.class, WebProperties.class})
public class TedeeInfrastructureConfiguration {

  public static final String EVENTS_PATH = "/tedee/events";
  public static final String CALLBACK_SECRET_HEADER = "X-Tedee-Callback-Secret";

  @Bean
  ApplicationLocale applicationLocale(final WebProperties webProperties) {
    return new ApplicationLocale(webProperties);
  }
}
