package org.sanmibuh.tedee.lock.domain.vo;

import org.sanmibuh.framework.scheduling.CronValidator;
import org.sanmibuh.tedee.lock.domain.exception.InvalidCronExpressionException;

public record CronExpression(String value) {

  public CronExpression {
    if (!CronValidator.isValid(value)) {
      throw new InvalidCronExpressionException(value);
    }
  }
}
