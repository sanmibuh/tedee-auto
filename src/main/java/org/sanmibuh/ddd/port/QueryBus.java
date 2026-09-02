package org.sanmibuh.ddd.port;

public interface QueryBus {

  <R> R dispatch(Query<R> query);
}
