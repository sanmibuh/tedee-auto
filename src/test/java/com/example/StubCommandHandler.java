package com.example;

import org.sanmibuh.cqrs.port.Command;
import org.sanmibuh.cqrs.port.CommandHandler;

public class StubCommandHandler implements CommandHandler<StubCommandHandler.StubCommand> {

  public boolean handled;

  @Override
  public void handle(final StubCommand command) {
    handled = true;
  }

  public record StubCommand() implements Command {}
}
