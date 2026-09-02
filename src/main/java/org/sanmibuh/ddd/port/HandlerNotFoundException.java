package org.sanmibuh.ddd.port;

import java.io.Serial;

public final class HandlerNotFoundException extends RuntimeException {

  @Serial private static final long serialVersionUID = 3945626113256578325L;

  public HandlerNotFoundException(final Class<?> type) {
    super("No handler registered for: " + type.getName());
  }
}
