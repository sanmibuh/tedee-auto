package org.sanmibuh.tedee.bridge.infrastructure.secondary;

import static org.assertj.core.api.BDDAssertions.then;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.sanmibuh.framework.resilience.RetryProperties;
import org.sanmibuh.tedee.lock.infrastructure.TedeeProperties;
import org.sanmibuh.tedee.lock.infrastructure.secondary.TedeeApiTokenGenerator;
import org.sanmibuh.tedee.lock.infrastructure.secondary.TedeeApiTokenInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

class BridgeCallbackConfigurationTest {

  private final ApplicationContextRunner runner =
      new ApplicationContextRunner()
          .withUserConfiguration(BridgeCallbackConfiguration.class, TestConfiguration.class);

  @Test
  @SneakyThrows
  void should_useSeparateConnections_whenQueryingAndMutatingCallbacks() {
    final var requests = new AtomicInteger();
    final Set<Integer> sourcePorts = ConcurrentHashMap.newKeySet();
    final var server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext(
        "/v1.0/callback",
        exchange -> {
          sourcePorts.add(exchange.getRemoteAddress().getPort());
          try {
            respond(exchange, requests.incrementAndGet());
          } catch (final IOException exception) {
            throw new UncheckedIOException(exception);
          }
        });
    server.start();
    final var properties =
        new TedeeProperties(
            URI.create("http://localhost:" + server.getAddress().getPort() + "/v1.0"),
            "secret-token",
            "callback-secret",
            new RetryProperties(2, 500L, 2.0, 5000L));
    try {
      runner
          .withPropertyValues(
              "sanmibuh.rest.tedee.base-url=" + properties.baseUrl(),
              "sanmibuh.rest.tedee.api-key=" + properties.apiKey(),
              "sanmibuh.rest.tedee.callback-secret=" + properties.callbackSecret(),
              "sanmibuh.rest.tedee.retry.max-retries=2",
              "sanmibuh.rest.tedee.retry.initial-interval=500",
              "sanmibuh.rest.tedee.retry.multiplier=2.0",
              "sanmibuh.rest.tedee.retry.max-interval=5000")
          .run(
              context -> {
                context.getBean(CallbackQueryApi.class).getCallbacks();
                context
                    .getBean(CallbackCommandApi.class)
                    .createCallback(new com.tedee.bridge.client.model.CallbackDetailsNoId());
              });

      then(requests.get()).isEqualTo(2);
      then(sourcePorts).hasSize(2);
    } finally {
      server.stop(0);
    }
  }

  private static void respond(final HttpExchange exchange, final int request) throws IOException {
    final var response = request == 1 ? "[]" : "{\"id\": 1}";
    exchange.getResponseHeaders().set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    exchange.sendResponseHeaders(200, response.length());
    exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
    exchange.close();
  }

  @Configuration
  @EnableConfigurationProperties(TedeeProperties.class)
  static class TestConfiguration {

    @Bean
    Clock clock() {
      return Clock.systemUTC();
    }

    @Bean
    TedeeApiTokenInterceptor tedeeApiTokenInterceptor(final Clock clock) {
      return new TedeeApiTokenInterceptor(new TedeeApiTokenGenerator("secret-token"), clock);
    }

    @Bean
    RestClient.Builder restClientBuilder() {
      return RestClient.builder();
    }
  }
}
