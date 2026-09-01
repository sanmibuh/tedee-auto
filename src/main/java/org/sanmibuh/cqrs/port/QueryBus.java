package org.sanmibuh.cqrs.port;

public interface QueryBus {

  <R> R dispatch(Query<R> query);
}
