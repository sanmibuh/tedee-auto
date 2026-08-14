package org.sanmibuh.tedee.automation.lock.domain;

import org.sanmibuh.ddd.domain.DomainException;

public class InvalidLockIdException extends DomainException {

  public InvalidLockIdException(int value) {
    super("Lock ID must be positive, got: " + value);
  }
}
