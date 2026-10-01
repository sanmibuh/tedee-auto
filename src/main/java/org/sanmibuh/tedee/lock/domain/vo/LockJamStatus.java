package org.sanmibuh.tedee.lock.domain.vo;

import org.sanmibuh.tedee.lock.domain.exception.InvalidLockJamStatusException;

public enum LockJamStatus {
  NOT_JAMMED(0),
  JAMMED(1);

  private final int bridgeCode;

  LockJamStatus(final int bridgeCode) {
    this.bridgeCode = bridgeCode;
  }

  public static LockJamStatus fromBridgeCode(final int bridgeCode) {
    for (final var status : values()) {
      if (status.bridgeCode == bridgeCode) {
        return status;
      }
    }
    throw new InvalidLockJamStatusException(bridgeCode);
  }
}
