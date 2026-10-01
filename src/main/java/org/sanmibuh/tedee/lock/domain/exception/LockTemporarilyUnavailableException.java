package org.sanmibuh.tedee.lock.domain.exception;

import org.sanmibuh.ddd.domain.TransientIntegrationException;

public final class LockTemporarilyUnavailableException extends TransientIntegrationException {

  public LockTemporarilyUnavailableException(final int deviceId, final Throwable cause) {
    super("Lock temporarily unavailable for device: " + deviceId, cause);
  }
}
