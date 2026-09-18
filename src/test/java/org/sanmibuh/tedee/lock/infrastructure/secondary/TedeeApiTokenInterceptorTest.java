package org.sanmibuh.tedee.lock.infrastructure.secondary;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;

@ExtendWith(MockitoExtension.class)
class TedeeApiTokenInterceptorTest {

  private final TedeeApiTokenInterceptor sut =
      new TedeeApiTokenInterceptor(
          new TedeeApiTokenGenerator("BE9xnPnGfVUS"),
          Clock.fixed(Instant.ofEpochMilli(1691058833000L), ZoneOffset.UTC));

  @Mock private HttpRequest request;

  @Mock private ClientHttpRequestExecution execution;

  @Test
  @SneakyThrows
  void should_setEncryptedApiTokenHeader_whenIntercepting() {
    final var headers = new HttpHeaders();
    given(request.getHeaders()).willReturn(headers);

    sut.intercept(request, new byte[0], execution);

    then(headers.getFirst("api_token"))
        .isEqualTo("e59d9763edc6e59f2faccf9a769e5cf170d68439c3fd67afae5e3e72d0463a711691058833000");
  }
}
