package com.example;

import org.sanmibuh.ddd.port.Command;
import org.sanmibuh.ddd.port.CommandHandler;

public class StubCommandHandler
    extends CommandHandler<StubCommandHandler.StubCommand, StubAggregate> {

  public boolean handled;

  @Override
  protected StubAggregate execute(final StubCommand command) {
    handled = true;
    return new StubAggregate();
  }

  public record StubCommand() implements Command {}
}
