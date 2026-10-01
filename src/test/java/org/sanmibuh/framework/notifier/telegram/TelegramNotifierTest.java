package org.sanmibuh.framework.notifier.telegram;

import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sanmibuh.ddd.domain.IntegrationException;
import org.sanmibuh.ddd.domain.TransientIntegrationException;
import org.sanmibuh.framework.notifier.Notifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

@RestClientTest
@Import(TelegramNotificationConfiguration.class)
@ContextConfiguration(classes = TelegramNotificationConfiguration.class)
@TestPropertySource(
    properties = {
      "sanmibuh.notification.telegram.base-url=http://telegram.local",
      "sanmibuh.notification.telegram.bot-token=bot-token",
      "sanmibuh.notification.telegram.chat-id=chat-id",
      "sanmibuh.notification.telegram.retry.max-retries=2",
      "sanmibuh.notification.telegram.retry.initial-interval=1",
      "sanmibuh.notification.telegram.retry.multiplier=1",
      "sanmibuh.notification.telegram.retry.max-interval=1"
    })
class TelegramNotifierTest {

  private static final String MESSAGE = "Lock closed";
  private static final int RETRY_ATTEMPTS = 3;
  private static final String SEND_MESSAGE_URL = "http://telegram.local/botbot-token/sendMessage";

  @Autowired private Notifier sut;

  @Autowired private MockRestServiceServer server;

  @Test
  void should_postMessageToConfiguredChat_whenNotifying() {
    server
        .expect(requestTo(SEND_MESSAGE_URL))
        .andExpect(method(HttpMethod.POST))
        .andExpect(content().json("{\"chat_id\":\"chat-id\",\"text\":\"Lock closed\"}"))
        .andRespond(withNoContent());

    sut.notify(MESSAGE);

    server.verify();
  }

  @Test
  void should_translateToIntegrationException_whenTelegramRejectsMessage() {
    server
        .expect(requestTo(SEND_MESSAGE_URL))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withStatus(HttpStatus.BAD_REQUEST));

    thenThrownBy(() -> sut.notify(MESSAGE)).isInstanceOf(IntegrationException.class).hasNoCause();

    server.verify();
  }

  @Test
  void should_retryTransientFailure_whenTelegramIsUnavailable() {
    server
        .expect(times(RETRY_ATTEMPTS), requestTo(SEND_MESSAGE_URL))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

    thenThrownBy(() -> sut.notify(MESSAGE)).isInstanceOf(TransientIntegrationException.class);

    server.verify();
  }

  @ParameterizedTest
  @ValueSource(ints = {429, 500, 502, 503, 504})
  void should_translateToTransientIntegrationException_whenTelegramRespondsWithServerError(
      final int statusCode) {
    server
        .expect(times(RETRY_ATTEMPTS), requestTo(SEND_MESSAGE_URL))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withStatus(HttpStatus.valueOf(statusCode)));

    thenThrownBy(() -> sut.notify(MESSAGE)).isInstanceOf(TransientIntegrationException.class);

    server.verify();
  }

  @Test
  void should_translateToTransientIntegrationException_whenTelegramIsUnreachable() {
    server
        .expect(times(RETRY_ATTEMPTS), requestTo(SEND_MESSAGE_URL))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withException(new IOException("Telegram unreachable")));

    thenThrownBy(() -> sut.notify(MESSAGE)).isInstanceOf(TransientIntegrationException.class);

    server.verify();
  }
}
