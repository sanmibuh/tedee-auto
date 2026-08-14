package org.sanmibuh.cqrs.infrastructure;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.sanmibuh.cqrs.domain.HandlerNotFoundException;
import org.sanmibuh.cqrs.domain.Query;
import org.sanmibuh.cqrs.domain.QueryHandler;

class SimpleQueryBusTest {

  record StubQuery() implements Query<String> {
  }

  static class StubQueryHandler implements QueryHandler<StubQuery, String> {

    @Override
    public String handle(final StubQuery query) {
      return "result";
    }
  }
  
  @Test
  void should_dispatchQuery_toRegisteredHandler_andReturnResult() {
    final var bus = new SimpleQueryBus(List.of(new StubQueryHandler()));

    final var result = bus.dispatch(new StubQuery());

    then(result).isEqualTo("result");
  }

  @Test
  void should_throwHandlerNotFoundException_whenNoHandlerRegistered() {
    final var bus = new SimpleQueryBus(List.of());

    thenThrownBy(() -> bus.dispatch(new StubQuery()))
        .isInstanceOf(HandlerNotFoundException.class)
        .hasMessageContaining(StubQuery.class.getName());
  }
}
