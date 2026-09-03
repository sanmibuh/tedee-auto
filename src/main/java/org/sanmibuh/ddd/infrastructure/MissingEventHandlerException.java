package org.sanmibuh.ddd.infrastructure;

public final class MissingEventHandlerException extends RuntimeException {

  public MissingEventHandlerException(final Class<?> eventType) {
    super("No handler registered for domain event: " + eventType.getName());
  }
}
