package org.sanmibuh.cqrs.domain;

public interface QueryHandler<Q extends Query<R>, R> {

  R handle(Q query);
}
