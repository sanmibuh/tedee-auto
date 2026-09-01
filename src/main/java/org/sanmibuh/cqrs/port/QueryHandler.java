package org.sanmibuh.cqrs.port;

public interface QueryHandler<Q extends Query<R>, R> {

  R handle(Q query);
}
