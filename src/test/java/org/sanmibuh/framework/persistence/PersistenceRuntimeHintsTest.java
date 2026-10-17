package org.sanmibuh.framework.persistence;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;

class PersistenceRuntimeHintsTest {

  private final PersistenceRuntimeHints sut = new PersistenceRuntimeHints();

  @Test
  void should_registerNamingStrategyForInstantiation_whenCalled() {
    final var hints = new RuntimeHints();

    sut.registerHints(hints, getClass().getClassLoader());

    then(RuntimeHintsPredicates.reflection()
            .onType(JpaEntityImplicitNamingStrategy.class)
            .withMemberCategories(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            .test(hints))
        .isTrue();
  }
}
