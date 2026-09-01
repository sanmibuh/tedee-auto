package org.sanmibuh.tedee.lock.domain;

import java.io.Serial;
import org.sanmibuh.ddd.domain.DomainException;

public final class InvalidLockIdException extends DomainException {

  @Serial private static final long serialVersionUID = 1L;

  public InvalidLockIdException(final int deviceId) {
    super("Invalid lock id: " + deviceId);
  }
}
