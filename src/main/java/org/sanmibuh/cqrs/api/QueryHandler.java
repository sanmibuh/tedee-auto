package org.sanmibuh.cqrs.api;

public interface QueryHandler<Q extends Query<R>, R> {

  R handle(Q query);
}
