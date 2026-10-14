package org.sanmibuh.tedee.lock.adapter.secondary;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.params.provider.Arguments.argumentSet;
import static org.mockito.BDDMockito.then;
import static org.sanmibuh.tedee.NotificationTestSupport.SPANISH;
import static org.sanmibuh.tedee.NotificationTestSupport.applicationLocale;
import static org.sanmibuh.tedee.NotificationTestSupport.messages;

import java.util.Locale;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.framework.notifier.Notifier;
import org.sanmibuh.tedee.lock.domain.event.LockStatusReported;
import org.sanmibuh.tedee.lock.domain.vo.LockJamStatus;
import org.sanmibuh.tedee.lock.domain.vo.LockState;

@ExtendWith(MockitoExtension.class)
class LockStatusReportedNotificationHandlerTest {

  @Mock Notifier notifier;

  static Stream<Arguments> localizedMessages() {
    return Stream.of(
        argumentSet("English", Locale.ENGLISH, "🔐 Lock 42 is locked", "🚨 The lock 42 is jammed!"),
        argumentSet(
            "Spanish",
            SPANISH,
            "🔐 La cerradura 42 está bloqueada",
            "🚨 ¡La cerradura 42 está atascada!"));
  }

  static Stream<Locale> locales() {
    return Stream.of(Locale.ENGLISH, SPANISH);
  }

  @ParameterizedTest
  @MethodSource("localizedMessages")
  void should_notifyLocalizedState_whenHandlingReportedClosedState(
      final Locale locale, final String stateMessage, final String jamMessage) {
    final var sut =
        new LockStatusReportedNotificationHandler(notifier, messages(), applicationLocale(locale));

    sut.handle(new LockStatusReported(42, LockState.CLOSED, LockJamStatus.NOT_JAMMED));

    BDDMockito.then(notifier).should().notify(stateMessage);
  }

  @ParameterizedTest
  @MethodSource("localizedMessages")
  void should_notifyLocalizedJamAlert_whenHandlingJammedStatus(
      final Locale locale, final String stateMessage, final String jamMessage) {
    final var sut =
        new LockStatusReportedNotificationHandler(notifier, messages(), applicationLocale(locale));

    sut.handle(new LockStatusReported(42, LockState.CLOSED, LockJamStatus.JAMMED));

    BDDMockito.then(notifier).should().notify(jamMessage);
  }

  @ParameterizedTest
  @MethodSource("locales")
  void should_defineMessageForEveryState_whenLoadingLocalizedCatalog(final Locale locale) {
    final var messages = messages();

    for (final var state : LockState.values()) {
      final var message =
          messages.getMessage("lock.state." + state.name().toLowerCase(Locale.ROOT), null, locale);
      then(message).isNotBlank();
    }
  }
}
