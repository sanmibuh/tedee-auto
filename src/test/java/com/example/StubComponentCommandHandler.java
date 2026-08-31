package com.example;

import org.sanmibuh.cqrs.api.Command;
import org.sanmibuh.cqrs.api.CommandHandler;
import org.springframework.stereotype.Component;

@Component
public class StubComponentCommandHandler
    implements CommandHandler<StubComponentCommandHandler.StubComponentCommand> {

  public boolean handled;

  @Override
  public void handle(final StubComponentCommand command) {
    handled = true;
  }

  public record StubComponentCommand() implements Command {}
}
