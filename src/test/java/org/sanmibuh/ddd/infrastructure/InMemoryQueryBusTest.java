package org.sanmibuh.ddd.infrastructure;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.sanmibuh.ddd.port.HandlerNotFoundException;
import org.sanmibuh.ddd.port.Query;
import org.sanmibuh.ddd.port.QueryHandler;

class InMemoryQueryBusTest {

  record StubQuery() implements Query<String> {}

  static class StubQueryHandler implements QueryHandler<StubQuery, String> {

    @Override
    public String handle(final StubQuery query) {
      return "result";
    }
  }

  @Test
  void should_returnQueryResult_whenHandlerIsRegistered() {
    final var bus = new InMemoryQueryBus(List.of(new StubQueryHandler()));

    final var result = bus.dispatch(new StubQuery());

    then(result).isEqualTo("result");
  }

  @Test
  void should_throwHandlerNotFoundException_whenNoHandlerRegistered() {
    final var bus = new InMemoryQueryBus(List.of());

    thenThrownBy(() -> bus.dispatch(new StubQuery()))
        .isInstanceOf(HandlerNotFoundException.class)
        .hasMessageContaining(StubQuery.class.getName());
  }
}
