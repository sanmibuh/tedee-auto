package org.sanmibuh.tedee.lock.infrastructure.secondary;

import static org.assertj.core.api.BDDAssertions.then;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.tedee.bridge.client.ApiClient;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.time.Clock;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.sanmibuh.framework.resilience.RetryProperties;
import org.sanmibuh.tedee.lock.infrastructure.TedeeProperties;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;

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
            Duration.ofSeconds(30),
            new RetryProperties(2, 500L, 2.0, 5000L));
    final var interceptor = sut.tedeeApiTokenInterceptor(properties, Clock.systemUTC());
    final var callbackQueryApi = sut.callbackQueryApi(properties, interceptor);
    final var callbackCommandApi = sut.callbackCommandApi(properties, interceptor);

    try {
      callbackQueryApi.getCallbacks();
      callbackCommandApi.createCallback(new com.tedee.bridge.client.model.CallbackDetailsNoId());

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
    exchange.getResponseBody().write(response.getBytes());
    exchange.close();
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
