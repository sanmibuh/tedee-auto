package org.sanmibuh.tedee.lock.infrastructure.secondary;

import static org.mockito.BDDMockito.then;
import static org.sanmibuh.tedee.NotificationTestSupport.applicationLocale;
import static org.sanmibuh.tedee.NotificationTestSupport.messages;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.framework.notifier.Notifier;
import org.sanmibuh.tedee.lock.domain.event.LockSettingsChanged;

@ExtendWith(MockitoExtension.class)
class LockSettingsChangedNotificationHandlerTest {

  @Mock Notifier notifier;

  @Test
  void should_notifySettingsChangedMessage_whenHandlingSettingsChangedLock() {
    final var sut =
        new LockSettingsChangedNotificationHandler(
            notifier, messages(), applicationLocale(Locale.ENGLISH));

    sut.handle(new LockSettingsChanged(42));

    then(notifier).should().notify("Lock 42 settings changed");
  }
}
