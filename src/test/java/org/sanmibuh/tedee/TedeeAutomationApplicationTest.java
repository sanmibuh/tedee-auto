package org.sanmibuh.tedee;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
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
import org.mockito.Answers;
import org.mockito.BDDMockito;
import org.sanmibuh.ddd.adapter.InMemoryCommandBus;
import org.sanmibuh.ddd.port.CommandBus;
import org.sanmibuh.framework.notifier.Notifier;
import org.sanmibuh.tedee.bridge.adapter.secondary.CallbackCommandApi;
import org.sanmibuh.tedee.bridge.adapter.secondary.CallbackQueryApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    properties = {
      "sanmibuh.server.public-url=http://automation.local:8080",
      "sanmibuh.rest.tedee.base-url=http://bridge.local/v1.0",
      "sanmibuh.rest.tedee.api-key=secret-token",
      "sanmibuh.rest.tedee.callback-secret=callback-secret",
      "sanmibuh.notification.telegram.bot-token=bot-token",
      "sanmibuh.notification.telegram.chat-id=chat-id"
    })
@ExtendWith(SoftAssertionsExtension.class)
class TedeeAutomationApplicationTest {

  @LocalServerPort private int port;

  @Autowired private CommandBus commandBus;

  @Autowired private Notifier notifier;

  @MockitoBean(answers = Answers.RETURNS_MOCKS, reset = MockReset.NONE)
  private CallbackQueryApi callbackQueryApi;

  @MockitoBean(answers = Answers.RETURNS_MOCKS, reset = MockReset.NONE)
  private CallbackCommandApi callbackCommandApi;

  @InjectSoftAssertions private BDDSoftAssertions softly;

  @Test
  void should_wireTheInMemoryCommandBus_whenApplicationStarts() {
    then(commandBus).isInstanceOf(InMemoryCommandBus.class);
  }

  @Test
  void should_wireTelegramNotifier_whenApplicationStarts() {
    then(notifier).isNotNull();
  }

  @Test
  void should_registerBridgeCallback_whenApplicationStarts() {
    BDDMockito.then(callbackQueryApi).should().getCallbacks();
    BDDMockito.then(callbackCommandApi).should().createCallback(any());
  }

  @Test
  @SneakyThrows
  void should_returnUp_whenApplicationStarts() {
    final var request =
        HttpRequest.newBuilder()
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
