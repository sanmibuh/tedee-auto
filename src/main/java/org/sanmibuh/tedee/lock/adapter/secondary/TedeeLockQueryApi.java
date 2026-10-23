package org.sanmibuh.tedee.lock.adapter.secondary;

import com.tedee.bridge.client.api.LockApi;
import com.tedee.bridge.client.model.LockDetails;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class TedeeLockQueryApi implements LockQueryApi {

  private final LockApi lockApi;

  @Override
  public LockDetails getLock(final int deviceId) {
    throw new UnsupportedOperationException();
  }
}
