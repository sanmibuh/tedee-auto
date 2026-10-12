package org.sanmibuh.tedee.bridge.infrastructure.secondary;

import static org.mockito.BDDMockito.then;
import static org.sanmibuh.tedee.NotificationTestSupport.applicationLocale;
import static org.sanmibuh.tedee.NotificationTestSupport.messages;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.framework.notifier.Notifier;
import org.sanmibuh.tedee.bridge.domain.event.BridgeConnected;

@ExtendWith(MockitoExtension.class)
class BridgeConnectedNotificationHandlerTest {

  @Mock Notifier notifier;

  @Test
  void should_notifyConnectedMessage_whenBridgeConnects() {
    final var sut =
        new BridgeConnectedNotificationHandler(
            notifier, messages(), applicationLocale(Locale.ENGLISH));

    sut.handle(new BridgeConnected());

    then(notifier).should().notify("Bridge is connected");
  }
}
