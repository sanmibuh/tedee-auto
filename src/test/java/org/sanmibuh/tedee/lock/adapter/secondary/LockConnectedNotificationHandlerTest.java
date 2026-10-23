package org.sanmibuh.tedee.lock.adapter.secondary;

import static org.mockito.BDDMockito.then;
import static org.sanmibuh.tedee.NotificationTestSupport.applicationLocale;
import static org.sanmibuh.tedee.NotificationTestSupport.messages;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.framework.notifier.Notifier;
import org.sanmibuh.tedee.lock.domain.event.LockConnected;

@ExtendWith(MockitoExtension.class)
class LockConnectedNotificationHandlerTest {

  @Mock Notifier notifier;

  @Test
  void should_notifyConnectedMessage_whenHandlingConnectedLock() {
    final var sut =
        new LockConnectedNotificationHandler(
            notifier, messages(), applicationLocale(Locale.ENGLISH));

    sut.handle(new LockConnected(42, "Front door"));

    then(notifier).should().notify("Lock 42 is connected");
  }
}
