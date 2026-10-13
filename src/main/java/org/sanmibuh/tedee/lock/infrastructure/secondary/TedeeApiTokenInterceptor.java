package org.sanmibuh.tedee.lock.infrastructure.secondary;

import java.io.IOException;
import java.time.Clock;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

@RequiredArgsConstructor
public final class TedeeApiTokenInterceptor implements ClientHttpRequestInterceptor {

  private static final String API_TOKEN_HEADER = "api_token";

  private final TedeeApiTokenGenerator generator;
  private final Clock clock;
  private final AtomicLong lastTimestamp = new AtomicLong();

  @Override
  public ClientHttpResponse intercept(
      final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution)
      throws IOException {
    final var headers = request.getHeaders();
    headers.set(API_TOKEN_HEADER, generator.generate(nextTimestamp()));
    return execution.execute(request, body);
  }

  private long nextTimestamp() {
    return lastTimestamp.updateAndGet(timestamp -> Math.max(clock.millis(), timestamp + 1));
  }
}
