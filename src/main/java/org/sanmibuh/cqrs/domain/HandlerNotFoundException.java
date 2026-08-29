package org.sanmibuh.cqrs.domain;

import java.io.Serial;

public class HandlerNotFoundException extends RuntimeException {

  @Serial private static final long serialVersionUID = 3945626113256578325L;

  public HandlerNotFoundException(final Class<?> type) {
    super("No handler registered for: " + type.getName());
  }
}
