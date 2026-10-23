package org.sanmibuh.framework.persistence;

import org.jspecify.annotations.Nullable;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public final class PersistenceRuntimeHints implements RuntimeHintsRegistrar {

  @Override
  public void registerHints(final RuntimeHints hints, final @Nullable ClassLoader classLoader) {
    hints
        .reflection()
        .registerType(
            JpaEntityImplicitNamingStrategy.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
  }
}
