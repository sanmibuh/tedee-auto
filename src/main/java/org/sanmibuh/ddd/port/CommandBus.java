package org.sanmibuh.ddd.port;

public interface CommandBus {

  void dispatch(Command command);
}
