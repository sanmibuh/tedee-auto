package org.sanmibuh.tedee;

import org.jspecify.annotations.Nullable;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

final class ServerRuntimeHints implements RuntimeHintsRegistrar {

  @Override
  public void registerHints(final RuntimeHints hints, final @Nullable ClassLoader classLoader) {
    hints
        .reflection()
        .registerType(
            ServerProperties.class,
            MemberCategory.ACCESS_DECLARED_FIELDS,
            MemberCategory.INVOKE_DECLARED_METHODS);
  }
}
