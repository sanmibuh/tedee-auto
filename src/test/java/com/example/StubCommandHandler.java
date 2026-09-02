package com.example;

import org.sanmibuh.cqrs.port.Command;
import org.sanmibuh.cqrs.port.CommandHandler;

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
