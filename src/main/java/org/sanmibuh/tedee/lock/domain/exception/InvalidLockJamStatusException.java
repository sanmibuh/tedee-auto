package org.sanmibuh.tedee.lock.domain.exception;

import org.sanmibuh.ddd.domain.DomainException;

public final class InvalidLockJamStatusException extends DomainException {

  public InvalidLockJamStatusException(final int bridgeCode) {
    super("Unknown lock jam status: " + bridgeCode);
  }
}
