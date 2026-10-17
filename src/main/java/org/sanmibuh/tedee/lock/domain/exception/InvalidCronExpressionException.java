package org.sanmibuh.tedee.lock.domain.exception;

import org.sanmibuh.ddd.domain.DomainException;

public final class InvalidCronExpressionException extends DomainException {

  public InvalidCronExpressionException(final String expression) {
    super("Invalid cron expression: " + expression);
  }
}
