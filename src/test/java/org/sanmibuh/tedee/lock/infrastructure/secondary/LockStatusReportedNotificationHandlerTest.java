package org.sanmibuh.tedee.lock.infrastructure.secondary;

import static org.mockito.BDDMockito.then;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.framework.notifier.Notifier;
import org.sanmibuh.tedee.lock.domain.event.LockStatusReported;
import org.sanmibuh.tedee.lock.domain.vo.LockJamStatus;
import org.sanmibuh.tedee.lock.domain.vo.LockState;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.context.support.StaticMessageSource;

@ExtendWith(MockitoExtension.class)
class LockStatusReportedNotificationHandlerTest {

  @Mock Notifier notifier;

  @Test
  void should_notifyLocalizedState_whenHandlingReportedStatus() {
    final var messages = new StaticMessageSource();
    messages.addMessage("lock.state.closed", Locale.ENGLISH, "Lock {0} is locked");
    final var sut = new LockStatusReportedNotificationHandler(notifier, messages, Locale.ENGLISH);

    sut.handle(new LockStatusReported(42, LockState.CLOSED, LockJamStatus.NOT_JAMMED));

    then(notifier).should().notify("Lock 42 is locked");
  }

  @ParameterizedTest
  @EnumSource(LockState.class)
  void should_notifyLocalizedState_whenHandlingEachReportedState(final LockState state) {
    final var messages = new StaticMessageSource();
    messages.addMessage(
        "lock.state." + state.name().toLowerCase(Locale.ROOT), Locale.ENGLISH, "Lock {0} state");
    final var sut = new LockStatusReportedNotificationHandler(notifier, messages, Locale.ENGLISH);

    sut.handle(new LockStatusReported(42, state, LockJamStatus.NOT_JAMMED));

    then(notifier).should().notify("Lock 42 state");
  }

  @Test
  void should_notifyLocalizedJamAlert_whenHandlingJammedStatus() {
    final var messages = new StaticMessageSource();
    messages.addMessage("lock.state.closed", Locale.ENGLISH, "Lock {0} is locked");
    messages.addMessage("lock.jammed", Locale.ENGLISH, "Lock {0} is jammed");
    final var sut = new LockStatusReportedNotificationHandler(notifier, messages, Locale.ENGLISH);

    sut.handle(new LockStatusReported(42, LockState.CLOSED, LockJamStatus.JAMMED));

    then(notifier).should().notify("Lock 42 is jammed");
  }

  @Test
  void should_notifySpanishState_whenHandlingReportedStatus() {
    final var messages = new ResourceBundleMessageSource();
    messages.setBasename("messages");
    final var sut =
        new LockStatusReportedNotificationHandler(notifier, messages, Locale.forLanguageTag("es"));

    sut.handle(new LockStatusReported(42, LockState.CLOSED, LockJamStatus.NOT_JAMMED));

    then(notifier).should().notify("La cerradura 42 esta bloqueada");
  }
}
