package org.sanmibuh.tedee.lock.domain.exception;

import org.sanmibuh.ddd.domain.DomainException;

public final class InvalidLockRequestException extends DomainException {

  public InvalidLockRequestException(final int deviceId, final Throwable cause) {
    super("Invalid lock request for device: " + deviceId, cause);
  }
}
