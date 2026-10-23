package org.sanmibuh.tedee.lock.adapter.secondary;

import com.tedee.bridge.client.model.LockDetails;

interface LockQueryApi {

  LockDetails getLock(int deviceId);
}
