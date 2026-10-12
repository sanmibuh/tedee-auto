package org.sanmibuh.tedee.lock.infrastructure.secondary;

import lombok.RequiredArgsConstructor;
import org.sanmibuh.framework.notifier.Notifier;
import org.sanmibuh.tedee.ApplicationLocale;
import org.sanmibuh.tedee.lock.infrastructure.primary.BridgeConnectionNotifier;
import org.springframework.context.MessageSource;

@RequiredArgsConstructor
public final class BridgeConnectionNotificationHandler implements BridgeConnectionNotifier {

  private final Notifier notifier;
  private final MessageSource messages;
  private final ApplicationLocale applicationLocale;

  @Override
  public void notifyConnected() {
    notifier.notify(messages.getMessage("bridge.connected", null, applicationLocale.current()));
  }

  @Override
  public void notifyDisconnected() {
    throw new UnsupportedOperationException();
  }
}
