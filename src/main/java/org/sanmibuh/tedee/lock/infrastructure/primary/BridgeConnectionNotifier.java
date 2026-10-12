package org.sanmibuh.tedee.lock.infrastructure.primary;

public interface BridgeConnectionNotifier {

  void notifyConnected();

  void notifyDisconnected();
}
