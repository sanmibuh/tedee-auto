package org.sanmibuh.tedee.automation.configuration;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.io.Serial;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sanmibuh.cqrs.domain.HandlerNotFoundException;
import org.sanmibuh.ddd.domain.DomainException;
import org.sanmibuh.tedee.TedeeAutomationApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.SneakyThrows;

@SpringBootTest(
  webEnvironment = RANDOM_PORT,
  classes = {
    TedeeAutomationApplication.class,
    GlobalExceptionHandlerTest.StubController.class
  })
@ExtendWith(SoftAssertionsExtension.class)
class GlobalExceptionHandlerTest {

  @LocalServerPort
  private int port;

  @InjectSoftAssertions
  private BDDSoftAssertions softly;

  @Test
  @SneakyThrows
  void should_returnBadRequestWithDetail_whenDomainExceptionIsThrown() {
    final var response = get("/test/domain-exception");

    softly.then(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    softly.then(response.body()).contains("domain rule violated");
  }

  @Test
  @SneakyThrows
  void should_return500_whenHandlerNotFoundExceptionIsThrown() {
    final var response = get("/test/handler-not-found");

    then(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

  @Test
  @SneakyThrows
  void should_returnProblemDetailWithoutInternalDetails_whenUnhandledExceptionIsThrown() {
    final var response = get("/test/unexpected-exception");

    softly.then(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    softly.then(response.headers().firstValue("Content-Type").orElse("")).contains("application/problem+json");
    softly.then(response.body()).doesNotContain("StubUnexpectedException");
    softly.then(response.body()).doesNotContain("something went wrong internally");
  }

  @SneakyThrows
  private HttpResponse<String> get(final String path) {
    final var request = HttpRequest.newBuilder()
      .uri(URI.create("http://localhost:" + port + path))
      .GET()
      .build();
    try (final var client = HttpClient.newHttpClient()) {
      return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
  }

  @RestController
  static class StubController {

    @GetMapping("/test/domain-exception")
    void throwDomainException() {
      throw new StubDomainException();
    }

    @GetMapping("/test/handler-not-found")
    void throwHandlerNotFoundException() {
      throw new HandlerNotFoundException(String.class);
    }

    @GetMapping("/test/unexpected-exception")
    void throwUnexpectedException() {
      throw new StubUnexpectedException();
    }

    static class StubDomainException extends DomainException {

      @Serial
      private static final long serialVersionUID = -1440054683212399969L;

      StubDomainException() {
        super("domain rule violated");
      }
    }

    static class StubUnexpectedException extends RuntimeException {

      @Serial
      private static final long serialVersionUID = 1L;

      StubUnexpectedException() {
        super("something went wrong internally");
      }
    }
  }
}
