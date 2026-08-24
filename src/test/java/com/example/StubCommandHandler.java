package com.example;

import org.sanmibuh.cqrs.domain.Command;
import org.sanmibuh.cqrs.domain.CommandHandler;

public class StubCommandHandler implements CommandHandler<StubCommandHandler.StubCommand> {

  public record StubCommand() implements Command {}

  public boolean handled;

  @Override
  public void handle(final StubCommand command) {
    handled = true;
  }
}
