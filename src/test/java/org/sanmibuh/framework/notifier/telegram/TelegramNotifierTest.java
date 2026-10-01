package org.sanmibuh.framework.notifier.telegram;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;

import org.junit.jupiter.api.Test;
import org.sanmibuh.framework.notifier.Notifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

@RestClientTest(TelegramNotifier.class)
@Import({TelegramNotificationConfiguration.class, TelegramNotifier.class})
@ContextConfiguration(classes = TelegramNotificationConfiguration.class)
@TestPropertySource(
    properties = {
      "sanmibuh.notification.telegram.bot-token=bot-token",
      "sanmibuh.notification.telegram.chat-id=chat-id"
    })
class TelegramNotifierTest {

  private static final String MESSAGE = "Lock closed";
  private static final String SEND_MESSAGE_URL =
      "https://api.telegram.org/botbot-token/sendMessage";

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
}
