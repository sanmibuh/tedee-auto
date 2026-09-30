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
  UNPULLING
}
