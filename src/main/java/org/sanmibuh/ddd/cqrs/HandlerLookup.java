package org.sanmibuh.ddd.cqrs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sanmibuh.cqrs.port.BaseCommandHandler;
import org.sanmibuh.cqrs.port.HandlerNotFoundException;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.GenericTypeResolver;

class HandlerLookup {

  private final Map<Class<?>, BaseCommandHandler<?, ?>> index;

  HandlerLookup(final List<BaseCommandHandler<?, ?>> handlers) {
    final Map<Class<?>, BaseCommandHandler<?, ?>> map = new HashMap<>();
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

  BaseCommandHandler<?, ?> find(final Class<?> commandType) {
    final var handler = index.get(commandType);
    if (handler == null) {
      throw new HandlerNotFoundException(commandType);
    }
    return handler;
  }
}
