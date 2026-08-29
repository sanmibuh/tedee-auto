package org.sanmibuh.cqrs.infrastructure;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.sanmibuh.cqrs.domain.HandlerNotFoundException;
import org.springframework.aop.framework.ProxyFactory;

class HandlerLookupTest {

  @Test
  void should_throwIllegalArgumentException_whenDuplicateHandlersForSameMessageType() {
    final var handler1 = new ConcreteStubHandler();
    final var handler2 = new AnotherConcreteStubHandler();

    final var thrown =
        thenThrownBy(() -> new HandlerLookup<>(List.of(handler1, handler2), StubHandler.class));

    thrown
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(StubMessage.class.getName());
  }

  @Test
  void should_returnHandler_whenMessageTypeIsRegistered() {
    final var handler = new ConcreteStubHandler();
    final var lookup = new HandlerLookup<>(List.of(handler), StubHandler.class);

    final var found = lookup.find(StubMessage.class);

    then(found).isSameAs(handler);
  }

  @Test
  void should_throwHandlerNotFoundException_whenNoHandlerRegistered() {
    final var lookup = new HandlerLookup<>(List.of(), StubHandler.class);

    thenThrownBy(() -> lookup.find(StubMessage.class))
        .isInstanceOf(HandlerNotFoundException.class)
        .hasMessageContaining(StubMessage.class.getName());
  }

  @Test
  void should_throwIllegalArgumentException_whenHandlerHasNoGenericType() {
    final var rawHandler = new RawStubHandler();

    thenThrownBy(() -> new HandlerLookup<>(List.of(rawHandler), StubHandler.class))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(RawStubHandler.class.getName());
  }

  @Test
  @SuppressWarnings("unchecked")
  void should_returnHandler_whenHandlerIsWrappedInJdkProxy() {
    final var handler = new ConcreteStubHandler();
    final var proxyFactory = new ProxyFactory(handler);
    proxyFactory.setProxyTargetClass(false);
    final var proxy = (StubHandler<StubMessage>) proxyFactory.getProxy();
    final var lookup = new HandlerLookup<>(List.of(proxy), StubHandler.class);

    final var found = lookup.find(StubMessage.class);

    then(found).isSameAs(proxy);
  }

  @Test
  @SuppressWarnings("unchecked")
  void should_returnHandler_whenHandlerIsWrappedInCglibProxy() {
    final var handler = new ConcreteStubHandler();
    final var proxyFactory = new ProxyFactory(handler);
    proxyFactory.setProxyTargetClass(true);
    final var proxy = (StubHandler<StubMessage>) proxyFactory.getProxy();
    final var lookup = new HandlerLookup<>(List.of(proxy), StubHandler.class);

    final var found = lookup.find(StubMessage.class);

    then(found).isSameAs(proxy);
  }

  @SuppressWarnings("unused")
  interface StubHandler<T> {}

  record StubMessage() {}

  static class ConcreteStubHandler implements StubHandler<StubMessage> {}

  @SuppressWarnings("rawtypes")
  static class RawStubHandler implements StubHandler {}

  static class AnotherConcreteStubHandler implements StubHandler<StubMessage> {}
}
