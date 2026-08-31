package org.sanmibuh.tedee.lock.infrastructure.secondary;

import com.tedee.bridge.client.model.InvalidToken;
import com.tedee.bridge.client.model.LockDetails;
import java.util.stream.Stream;
import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;

@ExtendWith(SoftAssertionsExtension.class)
class TedeeBridgeRuntimeHintsTest {

  private final TedeeBridgeRuntimeHints sut = new TedeeBridgeRuntimeHints();

  @InjectSoftAssertions private BDDSoftAssertions softly;

  static Stream<ClassLoader> classLoaders() {
    return Stream.of(TedeeBridgeRuntimeHintsTest.class.getClassLoader(), null);
  }

  @ParameterizedTest
  @MethodSource("classLoaders")
  void should_registerReflectionHints_whenCalled(final ClassLoader classLoader) {
    final var hints = new RuntimeHints();

    sut.registerHints(hints, classLoader);

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
