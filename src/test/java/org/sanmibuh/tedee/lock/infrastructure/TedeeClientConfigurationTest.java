package org.sanmibuh.tedee.lock.infrastructure;

import static org.assertj.core.api.BDDAssertions.then;

import com.tedee.bridge.client.ApiClient;
import com.tedee.bridge.client.auth.ApiKeyAuth;
import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.client.RestClient;

@ExtendWith(SoftAssertionsExtension.class)
class TedeeClientConfigurationTest {

  private final TedeeClientConfiguration sut = new TedeeClientConfiguration();

  @InjectSoftAssertions private BDDSoftAssertions softly;

  @Test
  void should_configureApiClientWithBaseUrlAndApiToken() {
    final var properties = new TedeeProperties("http://bridge.local/v1.0", "secret-token");

    final var apiClient = sut.tedeeApiClient(RestClient.builder(), properties);

    softly.then(apiClient.getBasePath()).isEqualTo("http://bridge.local/v1.0");
    softly
        .then(((ApiKeyAuth) apiClient.getAuthentication("api_token")).getApiKey())
        .isEqualTo("secret-token");
  }

  @Test
  void should_createLockApiBackedByGivenApiClient() {
    final var apiClient = new ApiClient();

    final var lockApi = sut.lockApi(apiClient);

    then(lockApi.getApiClient()).isSameAs(apiClient);
  }
}
