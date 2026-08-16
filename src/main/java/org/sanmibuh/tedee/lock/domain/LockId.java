package org.sanmibuh.tedee.lock.domain;

public record LockId(int deviceId) {

  public LockId {
    if (deviceId <= 0) {
      throw new InvalidLockIdException(deviceId);
    }
  }
}
