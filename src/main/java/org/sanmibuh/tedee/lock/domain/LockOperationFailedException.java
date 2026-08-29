package org.sanmibuh.tedee.lock.domain;

import org.sanmibuh.ddd.domain.IntegrationException;

public class LockOperationFailedException extends IntegrationException {

  public LockOperationFailedException(final int deviceId, final Throwable cause) {
    super("Lock operation failed for device: " + deviceId, cause);
  }
}
