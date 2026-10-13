package org.sanmibuh.tedee.bridge.infrastructure.primary;

import static org.sanmibuh.tedee.lock.infrastructure.TedeeInfrastructureConfiguration.EVENTS_PATH;

import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.sanmibuh.ddd.port.CommandBus;
import org.sanmibuh.tedee.ServerProperties;
import org.sanmibuh.tedee.bridge.application.AddBridgeCallbackCommand;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
final class TedeeCallbackRegistration implements SmartLifecycle {

  private final CommandBus commandBus;
  private final URI callbackUrl;
  private boolean running;

  TedeeCallbackRegistration(final CommandBus commandBus, final ServerProperties serverProperties) {
    this.commandBus = commandBus;
    callbackUrl =
        URI.create(
            UriComponentsBuilder.fromUriString(serverProperties.publicUrl())
                .path(EVENTS_PATH)
                .toUriString());
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
