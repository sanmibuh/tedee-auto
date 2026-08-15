package org.sanmibuh.cqrs.infrastructure;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.sanmibuh.cqrs.domain.HandlerNotFoundException;
import org.springframework.core.GenericTypeResolver;

class HandlerLookup<H> {

  private final Map<Class<?>, H> index;

  HandlerLookup(final List<H> handlers, final Class<?> handlerInterface) {
    index = handlers.stream()
      .collect(Collectors.toMap(h -> resolveMessageType(h.getClass(), handlerInterface), Function.identity()));
  }

  @SuppressWarnings("unchecked")
  <T extends H> T find(final Class<?> messageType) {
    final var handler = index.get(messageType);
    if (handler == null) {
      throw new HandlerNotFoundException(messageType);
    }
    return (T) handler;
  }

  private static Class<?> resolveMessageType(final Class<?> handlerClass, final Class<?> handlerInterface) {
    final var typeArgs = GenericTypeResolver.resolveTypeArguments(handlerClass, handlerInterface);
    if (typeArgs == null || typeArgs.length == 0) {
      throw new IllegalArgumentException("Cannot resolve message type for: " + handlerClass.getName());
    }
    return typeArgs[0];
  }
}
