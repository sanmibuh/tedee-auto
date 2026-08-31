package org.sanmibuh.cqrs.api;

public interface CommandBus {

  void dispatch(Command command);
}
