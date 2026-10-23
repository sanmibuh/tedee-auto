package org.sanmibuh.tedee.lock.adapter.secondary;

import com.tedee.bridge.client.api.LockApi;
import com.tedee.bridge.client.model.LockDetails;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class TedeeLockBridgeApi implements LockBridgeApi {

  private final LockApi lockApi;

  @Override
  public LockDetails getLock(final int deviceId) {
    return lockApi.getLockById(deviceId);
  }

  @Override
  public void lock(final int deviceId) {
    lockApi.postLock(deviceId);
  }
}
