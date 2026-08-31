package org.sanmibuh.cqrs.api;

public interface QueryBus {

  <R> R dispatch(Query<R> query);
}
