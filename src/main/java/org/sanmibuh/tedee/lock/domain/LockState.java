package org.sanmibuh.tedee.lock.domain;

public enum LockState {
  UNCALIBRATED(0),
  CALIBRATION(1),
  OPEN(2),
  PARTIALLY_OPEN(3),
  OPENING(4),
  CLOSING(5),
  CLOSED(6),
  PULL_SPRING(7),
  PULLING(8),
  UNKNOWN(9),
  UNPULLING(255);

  private final int bridgeCode;

  LockState(final int bridgeCode) {
    this.bridgeCode = bridgeCode;
  }

  public static LockState fromBridgeCode(final int bridgeCode) {
    for (final var state : values()) {
      if (state.bridgeCode == bridgeCode) {
        return state;
      }
    }
    throw new InvalidLockStateException(bridgeCode);
  }
}
