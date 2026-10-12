package org.sanmibuh.tedee.lock.infrastructure;

public interface BridgeConnectionNotifier {

  void notifyConnected();

  void notifyDisconnected();
}
