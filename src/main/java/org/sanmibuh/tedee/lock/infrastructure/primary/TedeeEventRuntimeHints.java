package org.sanmibuh.tedee.lock.infrastructure.primary;

import org.jspecify.annotations.Nullable;
import org.sanmibuh.tedee.lock.infrastructure.primary.TedeeEventType.BackendConnectionChangedData;
import org.sanmibuh.tedee.lock.infrastructure.primary.TedeeEventType.BatteryLevelChangedData;
import org.sanmibuh.tedee.lock.infrastructure.primary.TedeeEventType.DeviceConnectionChangedData;
import org.sanmibuh.tedee.lock.infrastructure.primary.TedeeEventType.DeviceSettingsChangedData;
import org.sanmibuh.tedee.lock.infrastructure.primary.TedeeEventType.LockStatusChangedData;
import org.springframework.aot.hint.BindingReflectionHintsRegistrar;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

final class TedeeEventRuntimeHints implements RuntimeHintsRegistrar {

  private final BindingReflectionHintsRegistrar bindingRegistrar =
      new BindingReflectionHintsRegistrar();

  @Override
  public void registerHints(final RuntimeHints hints, final @Nullable ClassLoader classLoader) {
    bindingRegistrar.registerReflectionHints(hints.reflection(), LockStatusChangedData.class);
    bindingRegistrar.registerReflectionHints(hints.reflection(), BatteryLevelChangedData.class);
    bindingRegistrar.registerReflectionHints(hints.reflection(), DeviceConnectionChangedData.class);
    bindingRegistrar.registerReflectionHints(hints.reflection(), DeviceSettingsChangedData.class);
    bindingRegistrar.registerReflectionHints(
        hints.reflection(), BackendConnectionChangedData.class);
  }
}
