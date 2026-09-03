package org.sanmibuh.ddd.infrastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sanmibuh.ddd.port.DomainEventHandler;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.GenericTypeResolver;

final class EventHandlerRegistry {

  private final Map<Class<?>, List<DomainEventHandler<?>>> index;

  EventHandlerRegistry(final List<DomainEventHandler<?>> handlers) {
    final Map<Class<?>, List<DomainEventHandler<?>>> map = new HashMap<>();
    for (final var handler : handlers) {
      final var eventType = resolveEventType(AopUtils.getTargetClass(handler));
      map.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
    }
    index = Map.copyOf(map);
  }

  private static Class<?> resolveEventType(final Class<?> handlerClass) {
    final var typeArgs =
        GenericTypeResolver.resolveTypeArguments(handlerClass, DomainEventHandler.class);
    if (typeArgs == null || typeArgs.length == 0) {
      throw new IllegalArgumentException(
          "Cannot resolve event type for: " + handlerClass.getName());
    }
    return typeArgs[0];
  }

  List<DomainEventHandler<?>> handlersFor(final Class<?> eventType) {
    return index.getOrDefault(eventType, List.of());
  }
}
