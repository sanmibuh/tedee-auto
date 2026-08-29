package org.sanmibuh.cqrs.infrastructure;

import java.util.List;
import org.sanmibuh.cqrs.domain.Query;
import org.sanmibuh.cqrs.domain.QueryBus;
import org.sanmibuh.cqrs.domain.QueryHandler;

public class SimpleQueryBus implements QueryBus {

  private final HandlerLookup<QueryHandler<?, ?>> lookup;

  public SimpleQueryBus(final List<QueryHandler<?, ?>> handlers) {
    lookup = new HandlerLookup<>(handlers, QueryHandler.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <R> R dispatch(final Query<R> query) {
    final var handler = (QueryHandler<Query<R>, R>) lookup.find(query.getClass());
    return handler.handle(query);
  }
}
