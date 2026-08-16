package org.sanmibuh.tedee.lock.domain;

import org.sanmibuh.ddd.domain.DomainException;

import java.io.Serial;

public class InvalidLockIdException extends DomainException {

  @Serial
  private static final long serialVersionUID = 1L;

  public InvalidLockIdException(final int deviceId) {
    super("Invalid lock id: " + deviceId);
  }
}
