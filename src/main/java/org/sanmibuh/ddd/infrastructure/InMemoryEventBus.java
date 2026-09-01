package org.sanmibuh.ddd.infrastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sanmibuh.ddd.domain.DomainEvent;
import org.sanmibuh.ddd.port.DomainEventHandler;
import org.sanmibuh.ddd.port.EventBus;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.GenericTypeResolver;

public class InMemoryEventBus implements EventBus {

  private final Map<Class<?>, List<DomainEventHandler<?>>> index;

  public InMemoryEventBus(final List<DomainEventHandler<?>> handlers) {
    final Map<Class<?>, List<DomainEventHandler<?>>> map = new HashMap<>();
    for (final var handler : handlers) {
      final var eventType = resolveEventType(AopUtils.getTargetClass(handler));
      map.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
    }
    index = Map.copyOf(map);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void publish(final DomainEvent event) {
    final var handlers = index.getOrDefault(event.getClass(), List.of());
    for (final var handler : handlers) {
      ((DomainEventHandler<DomainEvent>) handler).handle(event);
    }
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
}
