package org.sanmibuh.tedee;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;

class ServerRuntimeHintsTest {

  private final ServerRuntimeHints sut = new ServerRuntimeHints();

  @Test
  void should_registerServerPropertiesForValidation_whenCalled() {
    final var hints = new RuntimeHints();

    sut.registerHints(hints, getClass().getClassLoader());

    then(RuntimeHintsPredicates.reflection()
            .onType(ServerProperties.class)
            .withMemberCategories(
                MemberCategory.ACCESS_DECLARED_FIELDS, MemberCategory.INVOKE_DECLARED_METHODS)
            .test(hints))
        .isTrue();
  }
}
