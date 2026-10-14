package org.sanmibuh.tedee.bridge.infrastructure.secondary;

import com.tedee.bridge.client.ApiClient;
import com.tedee.bridge.client.api.CallbackApi;
import org.sanmibuh.tedee.shared.infrastructure.TedeeProperties;
import org.sanmibuh.tedee.shared.infrastructure.secondary.TedeeApiTokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class BridgeCallbackConfiguration {

  @Bean
  CallbackQueryApi callbackQueryApi(
      final TedeeProperties properties, final TedeeApiTokenInterceptor interceptor) {
    return new TedeeCallbackQueryApi(new CallbackApi(callbackApiClient(properties, interceptor)));
  }

  @Bean
  CallbackCommandApi callbackCommandApi(
      final TedeeProperties properties, final TedeeApiTokenInterceptor interceptor) {
    return new TedeeCallbackCommandApi(new CallbackApi(callbackApiClient(properties, interceptor)));
  }

  private ApiClient callbackApiClient(
      final TedeeProperties properties, final TedeeApiTokenInterceptor interceptor) {
    final var apiClient =
        new ApiClient(RestClient.builder().requestInterceptor(interceptor).build());
    apiClient.setBasePath(properties.baseUrl().toString());
    return apiClient;
  }
}
