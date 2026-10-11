package org.sanmibuh.tedee.lock.domain.exception;

import org.sanmibuh.ddd.domain.DomainException;

public final class InvalidBatteryLevelException extends DomainException {

  public InvalidBatteryLevelException(final int level) {
    super("Battery level must be between 0 and 100: " + level);
  }
}
