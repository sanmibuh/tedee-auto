package org.sanmibuh.tedee.lock.infrastructure.secondary;

import static org.assertj.core.api.BDDAssertions.then;

import com.tedee.bridge.client.ApiClient;
import java.time.Clock;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class TedeeClientConfigurationTest {

  private final TedeeClientConfiguration sut = new TedeeClientConfiguration();

  @Test
  void should_configureApiClientBasePath_whenPropertiesAreProvided() {
    final var properties = new TedeeProperties("http://bridge.local/v1.0", "secret-token");

    final var apiClient = sut.tedeeApiClient(RestClient.builder(), properties, Clock.systemUTC());

    then(apiClient.getBasePath()).isEqualTo("http://bridge.local/v1.0");
  }

  @Test
  void should_createLockApi_whenApiClientIsProvided() {
    final var apiClient = new ApiClient();

    final var lockApi = sut.lockApi(apiClient);

    then(lockApi.getApiClient()).isSameAs(apiClient);
  }
}
