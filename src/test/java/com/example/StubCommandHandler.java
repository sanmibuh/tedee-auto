package com.example;

import org.sanmibuh.cqrs.api.Command;
import org.sanmibuh.cqrs.api.CommandHandler;

public class StubCommandHandler implements CommandHandler<StubCommandHandler.StubCommand> {

  public boolean handled;

  @Override
  public void handle(final StubCommand command) {
    handled = true;
  }

  public record StubCommand() implements Command {}
}
