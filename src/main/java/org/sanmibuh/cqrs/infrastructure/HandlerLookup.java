package org.sanmibuh.cqrs.infrastructure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sanmibuh.cqrs.domain.HandlerNotFoundException;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.GenericTypeResolver;

class HandlerLookup<H> {

  private final Map<Class<?>, H> index;

  HandlerLookup(final List<H> handlers, final Class<?> handlerInterface) {
    final Map<Class<?>, H> map = new HashMap<>();
    for (final var handler : handlers) {
      final var messageType =
          resolveMessageType(AopUtils.getTargetClass(handler), handlerInterface);
      final var existing = map.put(messageType, handler);
      if (existing != null) {
        throw new IllegalArgumentException(
            "Duplicate handlers for message type: " + messageType.getName());
      }
    }
    index = Map.copyOf(map);
  }

  private static Class<?> resolveMessageType(
      final Class<?> handlerClass, final Class<?> handlerInterface) {
    final var typeArgs = GenericTypeResolver.resolveTypeArguments(handlerClass, handlerInterface);
    if (typeArgs == null || typeArgs.length == 0) {
      throw new IllegalArgumentException(
          "Cannot resolve message type for: " + handlerClass.getName());
    }
    return typeArgs[0];
  }

  H find(final Class<?> messageType) {
    final var handler = index.get(messageType);
    if (handler == null) {
      throw new HandlerNotFoundException(messageType);
    }
    return handler;
  }
}
