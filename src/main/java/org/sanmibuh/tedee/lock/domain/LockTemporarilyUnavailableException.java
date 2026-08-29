package org.sanmibuh.tedee.lock.domain;

import org.sanmibuh.ddd.domain.TransientIntegrationException;

public class LockTemporarilyUnavailableException extends TransientIntegrationException {

  public LockTemporarilyUnavailableException(final int deviceId, final Throwable cause) {
    super("Lock temporarily unavailable for device: " + deviceId, cause);
  }
}
