package org.sanmibuh.tedee.bridge.adapter.secondary.event;

import static org.mockito.BDDMockito.then;
import static org.sanmibuh.tedee.NotificationTestSupport.applicationLocale;
import static org.sanmibuh.tedee.NotificationTestSupport.messages;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.framework.notifier.Notifier;
import org.sanmibuh.tedee.bridge.domain.event.BridgeDisconnected;

@ExtendWith(MockitoExtension.class)
class BridgeDisconnectedNotificationHandlerTest {

  @Mock Notifier notifier;

  @Test
  void should_notifyDisconnectedMessage_whenBridgeDisconnects() {
    final var sut =
        new BridgeDisconnectedNotificationHandler(
            notifier, messages(), applicationLocale(Locale.ENGLISH));

    sut.handle(new BridgeDisconnected());

    then(notifier).should().notify("Bridge is disconnected");
  }
}
