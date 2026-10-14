package org.sanmibuh.ddd.adapter;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.sanmibuh.ddd.port.Query;
import org.sanmibuh.ddd.port.QueryBus;
import org.sanmibuh.ddd.port.QueryHandler;

@Slf4j
public final class InMemoryQueryBus implements QueryBus {

  private final HandlerLookup<QueryHandler<?, ?>> lookup;

  public InMemoryQueryBus(final List<QueryHandler<?, ?>> handlers) {
    lookup = new HandlerLookup<>(handlers, QueryHandler.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <R> R dispatch(final Query<R> query) {
    log.info("Dispatching query {}", query);
    final QueryHandler<Query<R>, R> handler =
        (QueryHandler<Query<R>, R>) lookup.find(query.getClass());
    final var response = handler.handle(query);
    log.info("Query response {}", response);

    return response;
  }
}
