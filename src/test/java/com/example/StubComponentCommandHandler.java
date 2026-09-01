package com.example;

import org.sanmibuh.cqrs.port.Command;
import org.sanmibuh.cqrs.port.CommandHandler;
import org.springframework.stereotype.Component;

@Component
public class StubComponentCommandHandler
    extends CommandHandler<StubComponentCommandHandler.StubComponentCommand> {

  public boolean handled;

  @Override
  protected void execute(final StubComponentCommand command) {
    handled = true;
  }

  public record StubComponentCommand() implements Command {}
}
