package org.sanmibuh.tedee.bridge.infrastructure.primary;

import static java.time.Duration.ofSeconds;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.ddd.port.CommandBus;
import org.sanmibuh.framework.resilience.RetryProperties;
import org.sanmibuh.tedee.ServerProperties;
import org.sanmibuh.tedee.bridge.application.AddBridgeCallbackCommand;
import org.sanmibuh.tedee.lock.infrastructure.TedeeProperties;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.client.RestClientException;

@ExtendWith(MockitoExtension.class)
class TedeeCallbackRegistrationTest {

  @Mock CommandBus commandBus;

  @Mock Clock clock;

  @Mock TaskScheduler taskScheduler;

  @Test
  void should_dispatchAddBridgeCallback_whenStarting() {
    final var callbackUrl = URI.create("https://automation.local/tedee/events");
    final var sut =
        new TedeeCallbackRegistration(
            commandBus,
            new ServerProperties("https://automation.local"),
            new TedeeProperties(
                URI.create("https://bridge.local/v1.0"),
                "api-key",
                "callback-secret",
                ofSeconds(1),
                new RetryProperties(1, 1L, 1.0, 1L)),
            clock,
            taskScheduler);

    sut.start();

    verify(commandBus).dispatch(new AddBridgeCallbackCommand(callbackUrl));
  }

  @Test
  void should_scheduleRetry_whenDispatchingCallbackCommandFailsTemporarily() {
    final var callbackUrl = URI.create("https://automation.local/tedee/events");
    final var now = Instant.parse("2026-10-10T14:00:00Z");
    willThrow(new RestClientException("Bridge unavailable"))
        .given(commandBus)
        .dispatch(new AddBridgeCallbackCommand(callbackUrl));
    given(clock.instant()).willReturn(now);
    final var sut =
        new TedeeCallbackRegistration(
            commandBus,
            new ServerProperties("https://automation.local"),
            new TedeeProperties(
                URI.create("https://bridge.local/v1.0"),
                "api-key",
                "callback-secret",
                ofSeconds(1),
                new RetryProperties(1, 1L, 1.0, 1L)),
            clock,
            taskScheduler);

    sut.start();

    verify(taskScheduler).schedule(any(Runnable.class), eq(now.plusSeconds(1)));
  }

  @Test
  void should_dispatchAddBridgeCallback_whenReregisteringWhileRunning() {
    final var callbackUrl = URI.create("https://automation.local/tedee/events");
    final var sut =
        new TedeeCallbackRegistration(
            commandBus,
            new ServerProperties("https://automation.local"),
            new TedeeProperties(
                URI.create("https://bridge.local/v1.0"),
                "api-key",
                "callback-secret",
                ofSeconds(1),
                new RetryProperties(1, 1L, 1.0, 1L)),
            clock,
            taskScheduler);

    sut.start();
    sut.reregister();

    verify(commandBus, times(2)).dispatch(new AddBridgeCallbackCommand(callbackUrl));
  }
}
