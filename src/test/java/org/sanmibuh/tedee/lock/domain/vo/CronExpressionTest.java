package org.sanmibuh.tedee.lock.domain.vo;

import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sanmibuh.tedee.lock.domain.exception.InvalidCronExpressionException;

class CronExpressionTest {

  @ParameterizedTest
  @ValueSource(
      strings = {
        "",
        "0 0 22 * *",
        "60 0 22 * * *",
        "0 0 24 * * *",
        "0 0 22 * 13 *",
        "0 0 22 * * MON-FUNDAY",
        "0 */0 * * * *",
        "0 0 22 32W * *",
        "0 0 22 L-0 * *",
        "0 0 22 * * 8L",
        "0 0 22 * * MON#0",
        "0 0 22 * * FRI-MON",
        "0 0 22 * * MON-FUNDAY",
        "0 0 22 * FOO *",
        "0 0 22 * * ?-MON",
        "0 0 22 * * 1-2-3",
        "0 0 22 * * */",
        "0 0 22 * * 1/2/3",
        "0 0 22 * * 1,,2",
        "0 0 22 lw * *",
        "0\t0 22 * * *"
      })
  void should_throwException_whenExpressionIsInvalid(final String value) {
    thenThrownBy(() -> new CronExpression(value))
        .isInstanceOf(InvalidCronExpressionException.class);
  }
}
