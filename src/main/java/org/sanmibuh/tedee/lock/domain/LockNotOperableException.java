package org.sanmibuh.tedee.lock.domain;

import org.sanmibuh.ddd.domain.DomainException;

public class LockNotOperableException extends DomainException {

  public LockNotOperableException(final LockId id, final LockStatus status) {
    super("Lock " + id.deviceId() + " is not operable in status " + status);
  }
}
