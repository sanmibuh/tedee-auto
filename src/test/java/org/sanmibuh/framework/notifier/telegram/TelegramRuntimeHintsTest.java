package org.sanmibuh.framework.notifier.telegram;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
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
            .withMemberCategories(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            .test(hints))
        .isTrue();
  }
}
