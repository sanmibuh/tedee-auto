package org.sanmibuh.tedee.lock.domain.exception;

import org.sanmibuh.ddd.domain.DomainException;

public final class InvalidLockStateException extends DomainException {

  public InvalidLockStateException(final int bridgeCode) {
    super("Unknown lock state: " + bridgeCode);
  }
}
