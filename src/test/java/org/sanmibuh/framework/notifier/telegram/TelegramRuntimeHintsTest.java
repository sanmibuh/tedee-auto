package org.sanmibuh.framework.notifier.telegram;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.sanmibuh.framework.resilience.RetryProperties;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;

class TelegramRuntimeHintsTest {

  private final TelegramRuntimeHints sut = new TelegramRuntimeHints();

  @Test
  void should_registerTelegramRequestForJackson_whenCalled() {
    final var hints = new RuntimeHints();

    sut.registerHints(hints, getClass().getClassLoader());

    then(RuntimeHintsPredicates.reflection()
            .onType(TelegramMessageRequest.class)
            .withMemberCategories(
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS)
            .test(hints))
        .isTrue();
  }

  @Test
  void should_registerTelegramPropertiesForValidation_whenCalled() {
    final var hints = new RuntimeHints();

    sut.registerHints(hints, getClass().getClassLoader());

    then(RuntimeHintsPredicates.reflection()
            .onType(TelegramProperties.class)
            .withMemberCategories(
                MemberCategory.ACCESS_DECLARED_FIELDS, MemberCategory.INVOKE_DECLARED_METHODS)
            .test(hints))
        .isTrue();
  }

  @Test
  void should_registerRetryPropertiesForValidation_whenCalled() {
    final var hints = new RuntimeHints();

    sut.registerHints(hints, getClass().getClassLoader());

    then(RuntimeHintsPredicates.reflection()
            .onType(RetryProperties.class)
            .withMemberCategories(
                MemberCategory.ACCESS_DECLARED_FIELDS, MemberCategory.INVOKE_DECLARED_METHODS)
            .test(hints))
        .isTrue();
  }
}
