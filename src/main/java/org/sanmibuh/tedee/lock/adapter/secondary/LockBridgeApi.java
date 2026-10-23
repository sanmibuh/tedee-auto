package org.sanmibuh.tedee.lock.adapter.secondary;

import com.tedee.bridge.client.model.LockDetails;

interface LockBridgeApi {

  LockDetails getLock(int deviceId);

  void lock(int deviceId);
}
