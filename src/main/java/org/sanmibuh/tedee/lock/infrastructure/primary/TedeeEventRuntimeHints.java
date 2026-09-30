package org.sanmibuh.tedee.lock.infrastructure.primary;

import org.jspecify.annotations.Nullable;
import org.sanmibuh.tedee.lock.infrastructure.primary.TedeeEventController.LockStatusChangedData;
import org.springframework.aot.hint.BindingReflectionHintsRegistrar;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

final class TedeeEventRuntimeHints implements RuntimeHintsRegistrar {

  private final BindingReflectionHintsRegistrar bindingRegistrar =
      new BindingReflectionHintsRegistrar();

  @Override
  public void registerHints(final RuntimeHints hints, final @Nullable ClassLoader classLoader) {
    bindingRegistrar.registerReflectionHints(hints.reflection(), LockStatusChangedData.class);
  }
}
