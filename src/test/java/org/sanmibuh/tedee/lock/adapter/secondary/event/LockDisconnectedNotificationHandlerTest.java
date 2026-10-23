package org.sanmibuh.tedee.lock.adapter.secondary.event;

import static org.mockito.BDDMockito.then;
import static org.sanmibuh.tedee.NotificationTestSupport.applicationLocale;
import static org.sanmibuh.tedee.NotificationTestSupport.messages;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.framework.notifier.Notifier;
import org.sanmibuh.tedee.lock.domain.event.LockDisconnected;

@ExtendWith(MockitoExtension.class)
class LockDisconnectedNotificationHandlerTest {

  @Mock Notifier notifier;

  @Test
  void should_notifyDisconnectedMessage_whenHandlingDisconnectedLock() {
    final var sut =
        new LockDisconnectedNotificationHandler(
            notifier, messages(), applicationLocale(Locale.ENGLISH));

    sut.handle(new LockDisconnected(42, "Front door"));

    then(notifier).should().notify("Lock Front door is disconnected");
  }
}
