package org.sanmibuh.ddd.port;

public interface QueryHandler<Q extends Query<R>, R> {

  R handle(Q query);
}
