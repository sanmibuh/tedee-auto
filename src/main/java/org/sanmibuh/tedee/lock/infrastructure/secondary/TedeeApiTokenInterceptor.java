package org.sanmibuh.tedee.lock.infrastructure.secondary;

import java.io.IOException;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

@RequiredArgsConstructor
final class TedeeApiTokenInterceptor implements ClientHttpRequestInterceptor {

  private static final String API_TOKEN_HEADER = "api_token";

  private final TedeeApiTokenGenerator generator;
  private final Clock clock;

  @Override
  public ClientHttpResponse intercept(
      final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution)
      throws IOException {
    final var headers = request.getHeaders();
    headers.set(API_TOKEN_HEADER, generator.generate(clock.millis()));
    // Tedee Bridge closes reused HTTP/1.1 connections without responding:
    // https://community.tedee.com/t/tedee-bridge-closes-persistent-http-connection-before-responding-to-callback-post/793
    headers.set(HttpHeaders.CONNECTION, "close");
    return execution.execute(request, body);
  }
}
