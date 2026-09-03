package org.sanmibuh.ddd.infrastructure;

public final class HandlerNotFoundException extends RuntimeException {

  public HandlerNotFoundException(final Class<?> type) {
    super("No handler registered for: " + type.getName());
  }
}
