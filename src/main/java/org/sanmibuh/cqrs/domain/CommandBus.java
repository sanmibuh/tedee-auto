package org.sanmibuh.cqrs.domain;

public interface CommandBus {

  void dispatch(Command command);
}
