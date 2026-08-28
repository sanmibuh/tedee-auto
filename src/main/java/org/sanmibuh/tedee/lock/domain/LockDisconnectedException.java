package org.sanmibuh.tedee.lock.domain;

import java.io.Serial;
import org.sanmibuh.ddd.domain.DomainException;

public class LockDisconnectedException extends DomainException {

  @Serial private static final long serialVersionUID = 1L;

  public LockDisconnectedException(final int deviceId) {
    super("Lock disconnected: " + deviceId);
  }
}
