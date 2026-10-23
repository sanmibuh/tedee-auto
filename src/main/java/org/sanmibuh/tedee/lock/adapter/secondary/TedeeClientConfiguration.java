package org.sanmibuh.tedee.lock.adapter.secondary;

import com.tedee.bridge.client.ApiClient;
import com.tedee.bridge.client.api.LockApi;
import java.time.Clock;
import org.sanmibuh.framework.resilience.ResilienceRuntimeHints;
import org.sanmibuh.tedee.shared.adapter.TedeeProperties;
import org.sanmibuh.tedee.shared.adapter.secondary.TedeeApiTokenGenerator;
import org.sanmibuh.tedee.shared.adapter.secondary.TedeeApiTokenInterceptor;
import org.sanmibuh.tedee.shared.adapter.secondary.TedeeReflectionHints;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.web.client.RestClient;

@Configuration
@EnableResilientMethods
@ImportRuntimeHints({TedeeReflectionHints.class, ResilienceRuntimeHints.class})
public class TedeeClientConfiguration {

  @Bean
  TedeeApiTokenInterceptor tedeeApiTokenInterceptor(
      final TedeeProperties properties, final Clock clock) {
    return new TedeeApiTokenInterceptor(new TedeeApiTokenGenerator(properties.apiKey()), clock);
  }

  @Bean
  ApiClient tedeeApiClient(
      final RestClient.Builder builder,
      final TedeeProperties properties,
      final TedeeApiTokenInterceptor interceptor) {
    return apiClient(builder, properties, interceptor);
  }

  private ApiClient apiClient(
      final RestClient.Builder builder,
      final TedeeProperties properties,
      final TedeeApiTokenInterceptor interceptor) {
    final var apiClient = new ApiClient(builder.requestInterceptor(interceptor).build());
    apiClient.setBasePath(properties.baseUrl().toString());
    return apiClient;
  }

  @Bean
  LockBridgeApi lockBridgeApi(final ApiClient apiClient) {
    return new TedeeLockBridgeApi(new LockApi(apiClient));
  }
}
