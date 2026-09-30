package org.sanmibuh.tedee.lock.domain;

public enum LockState {
  UNCALIBRATED,
  CALIBRATION,
  OPEN,
  PARTIALLY_OPEN,
  OPENING,
  CLOSING,
  CLOSED,
  PULL_SPRING,
  PULLING,
  UNKNOWN,
  UNPULLING;

  public static LockState fromBridgeCode(final int bridgeCode) {
    return switch (bridgeCode) {
      case 0 -> UNCALIBRATED;
      case 1 -> CALIBRATION;
      case 2 -> OPEN;
      case 3 -> PARTIALLY_OPEN;
      case 4 -> OPENING;
      case 5 -> CLOSING;
      case 6 -> CLOSED;
      case 7 -> PULL_SPRING;
      case 8 -> PULLING;
      case 255 -> UNPULLING;
      default -> UNKNOWN;
    };
  }
}
