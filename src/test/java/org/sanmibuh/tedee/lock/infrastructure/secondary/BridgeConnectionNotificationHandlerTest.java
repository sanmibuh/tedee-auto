package org.sanmibuh.tedee.lock.infrastructure.secondary;

import static org.mockito.BDDMockito.then;
import static org.sanmibuh.tedee.lock.infrastructure.secondary.NotificationTestSupport.applicationLocale;
import static org.sanmibuh.tedee.lock.infrastructure.secondary.NotificationTestSupport.messages;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.framework.notifier.Notifier;

@ExtendWith(MockitoExtension.class)
class BridgeConnectionNotificationHandlerTest {

  @Mock Notifier notifier;

  @Test
  void should_notifyConnectedMessage_whenBridgeConnects() {
    final var sut =
        new BridgeConnectionNotificationHandler(
            notifier, messages(), applicationLocale(Locale.ENGLISH));

    sut.notifyConnected();

    then(notifier).should().notify("Bridge is connected");
  }

  @Test
  void should_notifyDisconnectedMessage_whenBridgeDisconnects() {
    final var sut =
        new BridgeConnectionNotificationHandler(
            notifier, messages(), applicationLocale(Locale.ENGLISH));

    sut.notifyDisconnected();

    then(notifier).should().notify("Bridge is disconnected");
  }
}
