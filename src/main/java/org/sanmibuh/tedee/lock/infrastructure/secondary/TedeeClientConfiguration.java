package org.sanmibuh.tedee.lock.infrastructure.secondary;

import com.tedee.bridge.client.ApiClient;
import com.tedee.bridge.client.api.LockApi;
import java.time.Clock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.web.client.RestClient;

@Configuration
@EnableResilientMethods
@EnableConfigurationProperties(TedeeProperties.class)
@ImportRuntimeHints(TedeeReflectionHints.class)
public class TedeeClientConfiguration {

  @Bean
  ApiClient tedeeApiClient(
      final RestClient.Builder builder, final TedeeProperties properties, final Clock clock) {
    final var interceptor =
        new TedeeApiTokenInterceptor(new TedeeApiTokenGenerator(properties.apiKey()), clock);
    final var apiClient = new ApiClient(builder.requestInterceptor(interceptor).build());
    apiClient.setBasePath(properties.baseUrl());
    return apiClient;
  }

  @Bean
  LockApi lockApi(final ApiClient apiClient) {
    return new LockApi(apiClient);
  }
}
