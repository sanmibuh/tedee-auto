package org.sanmibuh.tedee.lock.infrastructure.secondary;

import com.tedee.bridge.client.model.InvalidToken;
import com.tedee.bridge.client.model.LockDetails;
import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;

@ExtendWith(SoftAssertionsExtension.class)
class TedeeBridgeRuntimeHintsTest {

  private final TedeeBridgeRuntimeHints sut = new TedeeBridgeRuntimeHints();

  @InjectSoftAssertions private BDDSoftAssertions softly;

  @Test
  void should_registerReflectionHints_whenCalled() {
    final var hints = new RuntimeHints();

    sut.registerHints(hints, getClass().getClassLoader());

    softly
        .then(RuntimeHintsPredicates.reflection().onType(InvalidToken.class).test(hints))
        .as("top-level model class")
        .isTrue();
    softly
        .then(RuntimeHintsPredicates.reflection().onType(LockDetails.StateEnum.class).test(hints))
        .as("inner enum of a model class")
        .isTrue();
  }
}
