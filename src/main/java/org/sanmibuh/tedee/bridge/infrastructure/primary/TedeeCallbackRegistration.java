package org.sanmibuh.tedee.bridge.infrastructure.primary;

import static org.sanmibuh.tedee.lock.infrastructure.TedeeInfrastructureConfiguration.EVENTS_PATH;

import java.net.URI;
import org.sanmibuh.ddd.port.CommandBus;
import org.sanmibuh.tedee.ServerProperties;
import org.sanmibuh.tedee.bridge.application.AddBridgeCallbackCommand;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

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
    commandBus.dispatch(new AddBridgeCallbackCommand(callbackUrl));
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
