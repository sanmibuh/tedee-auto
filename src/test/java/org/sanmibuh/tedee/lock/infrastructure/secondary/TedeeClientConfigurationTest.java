package org.sanmibuh.tedee.lock.infrastructure.secondary;

import static org.assertj.core.api.BDDAssertions.then;

import com.tedee.bridge.client.ApiClient;
import java.net.URI;
import java.time.Clock;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.sanmibuh.framework.resilience.RetryProperties;
import org.sanmibuh.tedee.lock.infrastructure.TedeeProperties;
import org.springframework.web.client.RestClient;

class TedeeClientConfigurationTest {

  private final TedeeClientConfiguration sut = new TedeeClientConfiguration();

  @Test
  void should_configureApiClientBasePath_whenPropertiesAreProvided() {
    final var properties = properties();
    final var apiClient =
        sut.tedeeApiClient(
            RestClient.builder(),
            properties,
            sut.tedeeApiTokenInterceptor(properties, Clock.systemUTC()));

    then(apiClient.getBasePath()).isEqualTo("https://gateway.local/tedee/api/v1.0");
  }

  @Test
  void should_createLockApi_whenApiClientIsProvided() {
    final var apiClient = new ApiClient();

    final var lockApi = sut.lockApi(apiClient);

    then(lockApi.getApiClient()).isSameAs(apiClient);
  }

  @Test
  void should_createIsolatedCallbackApiClients_whenBridgeApisAreConfigured() {
    final var properties = properties();
    final var interceptor = sut.tedeeApiTokenInterceptor(properties, Clock.systemUTC());

    final var callbackQueryApi =
        sut.callbackQueryApi(RestClient.builder(), properties, interceptor);
    final var callbackCommandApi =
        sut.callbackCommandApi(RestClient.builder(), properties, interceptor);

    then(callbackQueryApi.getApiClient()).isNotSameAs(callbackCommandApi.getApiClient());
  }

  private static TedeeProperties properties() {
    return new TedeeProperties(
        URI.create("https://gateway.local/tedee/api/v1.0"),
        "secret-token",
        "callback-secret",
        Duration.ofSeconds(30),
        new RetryProperties(2, 500L, 2.0, 5000L));
  }
}
