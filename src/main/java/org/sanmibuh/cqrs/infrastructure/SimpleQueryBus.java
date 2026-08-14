package org.sanmibuh.cqrs.infrastructure;

import java.util.List;
import org.sanmibuh.cqrs.domain.Query;
import org.sanmibuh.cqrs.domain.QueryBus;
import org.sanmibuh.cqrs.domain.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class SimpleQueryBus implements QueryBus {

  private final HandlerLookup<QueryHandler<?, ?>> lookup;

  public SimpleQueryBus(final List<QueryHandler<?, ?>> handlers) {
    lookup = new HandlerLookup<>(handlers, QueryHandler.class);
  }

  @Override
  public <R> R dispatch(final Query<R> query) {
    final QueryHandler<Query<R>, R> handler = lookup.find(query.getClass());
    return handler.handle(query);
  }
}
