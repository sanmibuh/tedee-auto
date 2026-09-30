package org.sanmibuh.tedee.lock.infrastructure.secondary;

import com.tedee.bridge.client.api.CallbackApi;
import lombok.RequiredArgsConstructor;
import org.sanmibuh.tedee.ServerProperties;
import org.springframework.context.SmartLifecycle;

@RequiredArgsConstructor
final class TedeeCallbackRegistration implements SmartLifecycle {

  private final CallbackApi callbackApi;
  private final ServerProperties serverProperties;

  @Override
  public void start() {}

  @Override
  public void stop() {}

  @Override
  public boolean isRunning() {
    return false;
  }
}
