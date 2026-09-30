package org.sanmibuh.tedee.lock.infrastructure.primary;

import static org.sanmibuh.tedee.lock.infrastructure.TedeeInfrastructureConfiguration.EVENTS_PATH;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class TedeeWebhookConfiguration implements WebMvcConfigurer {

  private final TedeeWebhookAuthenticationInterceptor authenticationInterceptor;

  @Override
  public void addInterceptors(final InterceptorRegistry registry) {
    registry.addInterceptor(authenticationInterceptor).addPathPatterns(EVENTS_PATH);
  }
}
