package org.sanmibuh.ddd.adapter;

import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.port.Command;
import org.sanmibuh.ddd.port.CommandBus;
import org.sanmibuh.ddd.port.TransactionalCommandBus;

@RequiredArgsConstructor
public final class InMemoryTransactionalCommandBus implements TransactionalCommandBus {

  private final CommandBus delegate;

  @Override
  public void dispatch(final Command command) {
    delegate.dispatch(command);
  }
}
