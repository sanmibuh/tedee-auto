package org.sanmibuh.cqrs.infrastructure;

import java.util.List;
import org.sanmibuh.cqrs.port.Query;
import org.sanmibuh.cqrs.port.QueryBus;
import org.sanmibuh.cqrs.port.QueryHandler;

public final class InMemoryQueryBus implements QueryBus {

  private final HandlerLookup<QueryHandler<?, ?>> lookup;

  public InMemoryQueryBus(final List<QueryHandler<?, ?>> handlers) {
    lookup = new HandlerLookup<>(handlers, QueryHandler.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <R> R dispatch(final Query<R> query) {
    final var handler = (QueryHandler<Query<R>, R>) lookup.find(query.getClass());
    return handler.handle(query);
  }
}
