package org.sanmibuh.tedee.lock.adapter.secondary.rest;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.time.Clock;
import org.junit.jupiter.api.Test;
import org.sanmibuh.tedee.lock.domain.vo.LockId;
import org.sanmibuh.tedee.lock.domain.vo.LockState;
import org.sanmibuh.tedee.shared.adapter.TedeeProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class TedeeClientConfigurationTest {

  private static final int DEVICE_ID = 42;
  private static final String BASE_URL = "http://localhost/v1.0";

  private final ApplicationContextRunner sut =
      new ApplicationContextRunner()
          .withUserConfiguration(TedeeClientConfiguration.class, TestConfiguration.class)
          .withPropertyValues(
              "sanmibuh.rest.tedee.base-url=" + BASE_URL,
              "sanmibuh.rest.tedee.api-key=secret-token",
              "sanmibuh.rest.tedee.callback-secret=callback-secret",
              "sanmibuh.rest.tedee.retry.max-retries=2",
              "sanmibuh.rest.tedee.retry.initial-interval=1",
              "sanmibuh.rest.tedee.retry.multiplier=1",
              "sanmibuh.rest.tedee.retry.max-interval=1");

  @Test
  void should_provideGatewayThatReadsTheLock_whenContextStarts() {
    sut.run(
        context -> {
          context
              .getBean(MockBridge.class)
              .server()
              .expect(requestTo(BASE_URL + "/lock/" + DEVICE_ID))
              .andRespond(
                  withSuccess(
                      "{\"id\":%d,\"name\":\"Front door\",\"state\":6}".formatted(DEVICE_ID),
                      MediaType.APPLICATION_JSON));

          then(context.getBean(LockGateway.class).get(new LockId(DEVICE_ID)))
              .isEqualTo(new LockView(DEVICE_ID, "Front door", LockState.CLOSED));
        });
  }

  private record MockBridge(RestClient.Builder builder, MockRestServiceServer server) {

    static MockBridge create() {
      final var builder = RestClient.builder();
      return new MockBridge(builder, MockRestServiceServer.bindTo(builder).build());
    }
  }

  @Configuration
  @EnableConfigurationProperties(TedeeProperties.class)
  static class TestConfiguration {

    @Bean
    Clock clock() {
      return Clock.systemUTC();
    }

    @Bean
    MockBridge mockBridge() {
      return MockBridge.create();
    }

    @Bean
    RestClient.Builder restClientBuilder(final MockBridge mockBridge) {
      return mockBridge.builder();
    }
  }
}
