package org.sanmibuh.tedee.lock.infrastructure.primary;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.sanmibuh.tedee.lock.infrastructure.primary.TedeeEventController.LockStatusChangedData;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;

class TedeeEventRuntimeHintsTest {

  private final TedeeEventRuntimeHints sut = new TedeeEventRuntimeHints();

  @Test
  void should_registerLockStatusChangedDataForBinding_whenCalled() {
    final var hints = new RuntimeHints();

    sut.registerHints(hints, getClass().getClassLoader());

    then(RuntimeHintsPredicates.reflection()
            .onConstructorInvocation(LockStatusChangedData.class.getDeclaredConstructors()[0])
            .test(hints))
        .isTrue();
  }
}
