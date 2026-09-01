package org.sanmibuh.ddd.cqrs.infrastructure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sanmibuh.cqrs.port.BaseCommandHandler;
import org.sanmibuh.cqrs.port.HandlerNotFoundException;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.GenericTypeResolver;

final class HandlerLookup<H> {

  private final Map<Class<?>, H> index;

  HandlerLookup(final List<H> handlers) {
    final Map<Class<?>, H> map = new HashMap<>();
    for (final var handler : handlers) {
      final var typeArgs =
          GenericTypeResolver.resolveTypeArguments(
              AopUtils.getTargetClass(handler), BaseCommandHandler.class);
      if (typeArgs == null || typeArgs.length == 0) {
        throw new IllegalArgumentException(
            "Cannot resolve command type for: " + handler.getClass().getName());
      }
      final var existing = map.put(typeArgs[0], handler);
      if (existing != null) {
        throw new IllegalArgumentException(
            "Duplicate handlers for command type: " + typeArgs[0].getName());
      }
    }
    index = Map.copyOf(map);
  }

  H find(final Class<?> commandType) {
    final var handler = index.get(commandType);
    if (handler == null) {
      throw new HandlerNotFoundException(commandType);
    }
    return handler;
  }
}
