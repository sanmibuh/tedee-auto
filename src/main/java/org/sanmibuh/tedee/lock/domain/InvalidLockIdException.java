package org.sanmibuh.tedee.lock.domain;

import org.sanmibuh.ddd.domain.DomainException;

public final class InvalidLockIdException extends DomainException {

  public InvalidLockIdException(final int deviceId) {
    super("Invalid lock id: " + deviceId);
  }
}
