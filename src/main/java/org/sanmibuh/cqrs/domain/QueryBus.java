package org.sanmibuh.cqrs.domain;

public interface QueryBus {

  <R> R dispatch(Query<R> query);
}
