package org.sanmibuh.tedee.lock.infrastructure.secondary;

import static org.junit.jupiter.params.provider.Arguments.argumentSet;
import static org.mockito.BDDMockito.then;

import java.util.Locale;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.framework.notifier.Notifier;
import org.sanmibuh.tedee.lock.domain.event.LockStatusReported;
import org.sanmibuh.tedee.lock.domain.vo.LockJamStatus;
import org.sanmibuh.tedee.lock.domain.vo.LockState;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.context.support.StaticMessageSource;

@ExtendWith(MockitoExtension.class)
class LockStatusReportedNotificationHandlerTest {

  @Mock Notifier notifier;

  static Stream<Arguments> localizedStates() {
    return Stream.of(
        argumentSet(
            "English uncalibrated",
            Locale.ENGLISH,
            LockState.UNCALIBRATED,
            "🔧❌ Lock 42 is uncalibrated"),
        argumentSet(
            "English calibration",
            Locale.ENGLISH,
            LockState.CALIBRATION,
            "🔧🔄 Lock 42 is calibrating..."),
        argumentSet("English open", Locale.ENGLISH, LockState.OPEN, "🔓 Lock 42 is unlocked"),
        argumentSet(
            "English partially open",
            Locale.ENGLISH,
            LockState.PARTIALLY_OPEN,
            "🚪 Lock 42 is partially open"),
        argumentSet(
            "English opening", Locale.ENGLISH, LockState.OPENING, "🔓🔄 Lock 42 is unlocking..."),
        argumentSet(
            "English closing", Locale.ENGLISH, LockState.CLOSING, "🔐🔄 Lock 42 is locking..."),
        argumentSet("English closed", Locale.ENGLISH, LockState.CLOSED, "🔐 Lock 42 is locked"),
        argumentSet(
            "English pull spring",
            Locale.ENGLISH,
            LockState.PULL_SPRING,
            "🔑 Lock 42 has pull spring open"),
        argumentSet(
            "English pulling",
            Locale.ENGLISH,
            LockState.PULLING,
            "🔄🔑 Lock 42 is pulling spring..."),
        argumentSet(
            "English unknown", Locale.ENGLISH, LockState.UNKNOWN, "❓ Lock 42 has unknown state"),
        argumentSet(
            "English unpulling",
            Locale.ENGLISH,
            LockState.UNPULLING,
            "🔄🔑 Lock 42 is unpulling spring..."),
        argumentSet(
            "Spanish uncalibrated",
            Locale.forLanguageTag("es"),
            LockState.UNCALIBRATED,
            "🔧❌ La cerradura 42 está sin calibrar"),
        argumentSet(
            "Spanish calibration",
            Locale.forLanguageTag("es"),
            LockState.CALIBRATION,
            "🔧🔄 La cerradura 42 se está calibrando..."),
        argumentSet(
            "Spanish open",
            Locale.forLanguageTag("es"),
            LockState.OPEN,
            "🔓 La cerradura 42 está desbloqueada"),
        argumentSet(
            "Spanish partially open",
            Locale.forLanguageTag("es"),
            LockState.PARTIALLY_OPEN,
            "🚪 La cerradura 42 está parcialmente abierta"),
        argumentSet(
            "Spanish opening",
            Locale.forLanguageTag("es"),
            LockState.OPENING,
            "🔓🔄 La cerradura 42 se está desbloqueando..."),
        argumentSet(
            "Spanish closing",
            Locale.forLanguageTag("es"),
            LockState.CLOSING,
            "🔐🔄 La cerradura 42 se está bloqueando..."),
        argumentSet(
            "Spanish closed",
            Locale.forLanguageTag("es"),
            LockState.CLOSED,
            "🔐 La cerradura 42 está bloqueada"),
        argumentSet(
            "Spanish pull spring",
            Locale.forLanguageTag("es"),
            LockState.PULL_SPRING,
            "🔑 La cerradura 42 tiene el rebalón abierto"),
        argumentSet(
            "Spanish pulling",
            Locale.forLanguageTag("es"),
            LockState.PULLING,
            "🔄🔑 La cerradura 42 está abriendo el rebalón..."),
        argumentSet(
            "Spanish unknown",
            Locale.forLanguageTag("es"),
            LockState.UNKNOWN,
            "❓ La cerradura 42 tiene un estado desconocido"),
        argumentSet(
            "Spanish unpulling",
            Locale.forLanguageTag("es"),
            LockState.UNPULLING,
            "🔄🔑 La cerradura 42 está cerrando el rebalón..."));
  }

  @ParameterizedTest
  @MethodSource("localizedStates")
  void should_notifyLocalizedState_whenHandlingEachReportedState(
      final Locale locale, final LockState state, final String message) {
    final var messages = new ResourceBundleMessageSource();
    messages.setBasename("messages");
    messages.setDefaultEncoding("UTF-8");
    messages.setFallbackToSystemLocale(false);
    final var sut =
        new LockStatusReportedNotificationHandler(notifier, messages, webProperties(locale));

    sut.handle(new LockStatusReported(42, state, LockJamStatus.NOT_JAMMED));

    then(notifier).should().notify(message);
  }

  @Test
  void should_notifyLocalizedJamAlert_whenHandlingJammedStatus() {
    final var messages = new StaticMessageSource();
    messages.addMessage("lock.state.closed", Locale.ENGLISH, "Lock {0} is locked");
    messages.addMessage("lock.jammed", Locale.ENGLISH, "Lock {0} is jammed");
    final var sut =
        new LockStatusReportedNotificationHandler(
            notifier, messages, webProperties(Locale.ENGLISH));

    sut.handle(new LockStatusReported(42, LockState.CLOSED, LockJamStatus.JAMMED));

    then(notifier).should().notify("Lock 42 is jammed");
  }

  static Stream<Arguments> localizedJamAlerts() {
    return Stream.of(
        argumentSet("English", Locale.ENGLISH, "🚨 The lock 42 is jammed!"),
        argumentSet("Spanish", Locale.forLanguageTag("es"), "🚨 ¡La cerradura 42 está atascada!"));
  }

  @ParameterizedTest
  @MethodSource("localizedJamAlerts")
  void should_notifyLocalizedJamAlert_whenHandlingJammedStatus(
      final Locale locale, final String message) {
    final var messages = new ResourceBundleMessageSource();
    messages.setBasename("messages");
    messages.setDefaultEncoding("UTF-8");
    messages.setFallbackToSystemLocale(false);
    final var sut =
        new LockStatusReportedNotificationHandler(notifier, messages, webProperties(locale));

    sut.handle(new LockStatusReported(42, LockState.CLOSED, LockJamStatus.JAMMED));

    then(notifier).should().notify(message);
  }

  private WebProperties webProperties(final Locale locale) {
    final var webProperties = new WebProperties();
    webProperties.setLocale(locale);
    return webProperties;
  }
}
