package org.sanmibuh.tedee.shared.infrastructure.primary;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.sanmibuh.tedee.shared.infrastructure.primary.TedeeEventType.BackendConnectionChangedData;
import org.sanmibuh.tedee.shared.infrastructure.primary.TedeeEventType.BatteryLevelChangedData;
import org.sanmibuh.tedee.shared.infrastructure.primary.TedeeEventType.DeviceConnectionChangedData;
import org.sanmibuh.tedee.shared.infrastructure.primary.TedeeEventType.DeviceSettingsChangedData;
import org.sanmibuh.tedee.shared.infrastructure.primary.TedeeEventType.LockStatusChangedData;
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

  @Test
  void should_registerBatteryLevelChangedDataForBinding_whenCalled() {
    final var hints = new RuntimeHints();

    sut.registerHints(hints, getClass().getClassLoader());

    then(RuntimeHintsPredicates.reflection()
            .onConstructorInvocation(BatteryLevelChangedData.class.getDeclaredConstructors()[0])
            .test(hints))
        .isTrue();
  }

  @Test
  void should_registerDeviceConnectionChangedDataForBinding_whenCalled() {
    final var hints = new RuntimeHints();

    sut.registerHints(hints, getClass().getClassLoader());

    then(RuntimeHintsPredicates.reflection()
            .onConstructorInvocation(DeviceConnectionChangedData.class.getDeclaredConstructors()[0])
            .test(hints))
        .isTrue();
  }

  @Test
  void should_registerDeviceSettingsChangedDataForBinding_whenCalled() {
    final var hints = new RuntimeHints();

    sut.registerHints(hints, getClass().getClassLoader());

    then(RuntimeHintsPredicates.reflection()
            .onConstructorInvocation(DeviceSettingsChangedData.class.getDeclaredConstructors()[0])
            .test(hints))
        .isTrue();
  }

  @Test
  void should_registerBackendConnectionChangedDataForBinding_whenCalled() {
    final var hints = new RuntimeHints();

    sut.registerHints(hints, getClass().getClassLoader());

    then(RuntimeHintsPredicates.reflection()
            .onConstructorInvocation(
                BackendConnectionChangedData.class.getDeclaredConstructors()[0])
            .test(hints))
        .isTrue();
  }
}
