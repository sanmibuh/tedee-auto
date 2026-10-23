package org.sanmibuh.framework.scheduling;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CronValidatorTest {

  @ParameterizedTest
  @ValueSource(strings = {"0 30 21,23 * * *", "@daily"})
  void should_acceptExpression_whenItIsValid(final String expression) {
    then(CronValidator.isValid(expression)).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "0 0 22 * *"})
  void should_rejectExpression_whenItIsInvalid(final String expression) {
    then(CronValidator.isValid(expression)).isFalse();
  }
}
