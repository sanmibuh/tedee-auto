package org.sanmibuh.tedee.lock.domain;

import org.sanmibuh.ddd.domain.DomainException;

public class InvalidLockRequestException extends DomainException {

  public InvalidLockRequestException(final int deviceId, final Throwable cause) {
    super("Invalid lock request for device: " + deviceId, cause);
  }
}
