package org.sanmibuh.tedee.bridge.infrastructure.primary;

import static org.sanmibuh.tedee.lock.infrastructure.TedeeInfrastructureConfiguration.EVENTS_PATH;

import java.net.URI;
import java.time.Clock;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.sanmibuh.ddd.port.CommandBus;
import org.sanmibuh.tedee.ServerProperties;
import org.sanmibuh.tedee.bridge.application.AddBridgeCallbackCommand;
import org.sanmibuh.tedee.lock.infrastructure.TedeeProperties;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
final class TedeeCallbackRegistration implements SmartLifecycle {

  private final CommandBus commandBus;
  private final URI callbackUrl;
  private final Clock clock;
  private final TaskScheduler taskScheduler;
  private final Duration registrationRetryInterval;
  private boolean running;

  TedeeCallbackRegistration(
      final CommandBus commandBus,
      final ServerProperties serverProperties,
      final TedeeProperties tedeeProperties,
      final Clock clock,
      final TaskScheduler taskScheduler) {
    this.commandBus = commandBus;
    callbackUrl =
        URI.create(
            UriComponentsBuilder.fromUriString(serverProperties.publicUrl())
                .path(EVENTS_PATH)
                .toUriString());
    this.clock = clock;
    this.taskScheduler = taskScheduler;
    registrationRetryInterval = tedeeProperties.callbackRegistrationInterval();
  }

  @Override
  public synchronized void start() {
    running = true;
    registerCallback();
  }

  private void registerCallback() {
    try {
      commandBus.dispatch(new AddBridgeCallbackCommand(callbackUrl));
    } catch (final RestClientException exception) {
      log.warn("Could not register callback {} on the Tedee Bridge", callbackUrl, exception);
      scheduleRetry();
    }
  }

  @SuppressWarnings("FutureReturnValueIgnored")
  private void scheduleRetry() {
    final var retryAt = clock.instant().plus(registrationRetryInterval);
    log.info("Scheduling callback registration retry for {} at {}", callbackUrl, retryAt);
    taskScheduler.schedule(this::reregister, retryAt);
  }

  synchronized void reregister() {
    if (running) {
      registerCallback();
    } else {
      log.info(
          "Skipping callback registration retry for {} because the service is stopped",
          callbackUrl);
    }
  }

  @Override
  public synchronized void stop() {
    running = false;
  }

  @Override
  public synchronized boolean isRunning() {
    return running;
  }
}
