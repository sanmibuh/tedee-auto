package org.sanmibuh.framework.scheduling;

import lombok.experimental.UtilityClass;
import org.springframework.scheduling.support.CronExpression;

@UtilityClass
public final class CronValidator {

  public static boolean isValid(final String expression) {
    return CronExpression.isValidExpression(expression);
  }
}
