package org.sanmibuh.tedee.lock.infrastructure.secondary;

import com.tedee.bridge.client.api.LockApi;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class TedeeLockCommandApi implements LockCommandApi {

  private final LockApi lockApi;

  @Override
  public void lock(final int deviceId) {
    lockApi.postLock(deviceId);
  }
}
