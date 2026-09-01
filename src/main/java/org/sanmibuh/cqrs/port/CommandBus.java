package org.sanmibuh.cqrs.port;

public interface CommandBus {

  void dispatch(Command command);
}
