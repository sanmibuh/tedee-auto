package com.example;

import org.sanmibuh.ddd.port.Command;
import org.sanmibuh.ddd.port.CommandHandler;
import org.springframework.stereotype.Component;

@Component
public class StubComponentCommandHandler
    extends CommandHandler<StubComponentCommandHandler.StubComponentCommand, StubAggregate> {

  public boolean handled;

  @Override
  protected StubAggregate execute(final StubComponentCommand command) {
    handled = true;
    return new StubAggregate();
  }

  public record StubComponentCommand() implements Command {}
}
