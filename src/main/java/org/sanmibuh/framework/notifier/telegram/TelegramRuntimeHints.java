package org.sanmibuh.framework.notifier.telegram;

import org.jspecify.annotations.Nullable;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

final class TelegramRuntimeHints implements RuntimeHintsRegistrar {

  @Override
  public void registerHints(final RuntimeHints hints, final @Nullable ClassLoader classLoader) {
    hints
        .reflection()
        .registerType(TelegramMessageRequest.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
  }
}
