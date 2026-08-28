package org.sanmibuh.tedee.lock.domain;

import java.io.Serial;
import org.sanmibuh.ddd.domain.DomainException;

public class UnexpectedBridgeErrorException extends DomainException {

  @Serial private static final long serialVersionUID = 1L;

  public UnexpectedBridgeErrorException(final int deviceId, final Throwable cause) {
    super("Unexpected Tedee bridge error while operating lock: " + deviceId, cause);
  }
}
