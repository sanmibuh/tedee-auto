package org.sanmibuh.tedee.lock.domain.vo;

import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sanmibuh.tedee.lock.domain.exception.InvalidCronExpressionException;

class CronExpressionTest {

  @ParameterizedTest
  @ValueSource(strings = {"", "0 0 22 * *"})
  void should_throwException_whenExpressionIsInvalid(final String value) {
    thenThrownBy(() -> new CronExpression(value))
        .isInstanceOf(InvalidCronExpressionException.class);
  }
}
