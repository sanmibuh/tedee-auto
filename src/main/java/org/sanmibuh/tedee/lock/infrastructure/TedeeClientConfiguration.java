package org.sanmibuh.tedee.lock.infrastructure;

import com.tedee.bridge.client.ApiClient;
import com.tedee.bridge.client.api.LockApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(TedeeProperties.class)
public class TedeeClientConfiguration {

  @Bean
  ApiClient tedeeApiClient(final RestClient.Builder builder, final TedeeProperties properties) {
    final var apiClient = new ApiClient(builder.build());
    apiClient.setBasePath(properties.baseUrl());
    return apiClient;
  }

  @Bean
  LockApi lockApi(final ApiClient apiClient) {
    return new LockApi(apiClient);
  }
}
