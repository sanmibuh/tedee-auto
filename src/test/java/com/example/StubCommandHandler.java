package com.example;

import org.sanmibuh.cqrs.port.Command;
import org.sanmibuh.cqrs.port.CommandHandler;

public class StubCommandHandler extends CommandHandler<StubCommandHandler.StubCommand> {

  public boolean handled;

  @Override
  protected void execute(final StubCommand command) {
    handled = true;
  }

  public record StubCommand() implements Command {}
}
