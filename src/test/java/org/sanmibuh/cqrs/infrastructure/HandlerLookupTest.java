package org.sanmibuh.cqrs.infrastructure;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.sanmibuh.cqrs.domain.HandlerNotFoundException;

class HandlerLookupTest {

  interface StubHandler<T> {
  }

  record StubMessage() {
  }

  static class ConcreteStubHandler implements StubHandler<StubMessage> {
  }

  @SuppressWarnings("rawtypes")
  static class RawStubHandler implements StubHandler {
  }

  @Test
  void should_findHandler_byMessageType() {
    final var handler = new ConcreteStubHandler();
    final var lookup = new HandlerLookup<>(List.of(handler), StubHandler.class);

    final StubHandler<StubMessage> found = lookup.find(StubMessage.class);

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
}
