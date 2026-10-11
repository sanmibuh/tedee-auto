package org.sanmibuh.framework.resilience;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;

class ResilienceRuntimeHintsTest {

  private final ResilienceRuntimeHints sut = new ResilienceRuntimeHints();

  @Test
  void should_registerRetryPropertiesForValidation_whenCalled() {
    final var hints = new RuntimeHints();

    sut.registerHints(hints, getClass().getClassLoader());

    then(RuntimeHintsPredicates.reflection()
            .onType(RetryProperties.class)
            .withMemberCategories(
                MemberCategory.ACCESS_DECLARED_FIELDS,
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS)
            .test(hints))
        .isTrue();
  }
}
