package org.sanmibuh.tedee.automation;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import lombok.SneakyThrows;
import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SoftAssertionsExtension.class)
class TedeeAutomationApplicationTest {

  @LocalServerPort
  private int port;

  @InjectSoftAssertions
  private BDDSoftAssertions softly;

  @Test
  @SneakyThrows
  void should_returnUp_whenApplicationStarts() {
    final var request = HttpRequest.newBuilder()
      .uri(URI.create("http://localhost:" + port + "/actuator/health"))
      .GET()
      .build();

    try (final var client = HttpClient.newHttpClient()) {
      final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

      softly.then(response.statusCode()).isEqualTo(200);
      softly.then(response.body()).contains("\"status\":\"UP\"");
    }
  }
}
