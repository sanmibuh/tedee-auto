package org.sanmibuh.tedee.lock.domain;

import java.io.Serial;
import org.sanmibuh.ddd.domain.DomainException;

public class LockNotFoundException extends DomainException {

  @Serial private static final long serialVersionUID = 1L;

  public LockNotFoundException(final int deviceId) {
    super("Lock not found: " + deviceId);
  }
}
