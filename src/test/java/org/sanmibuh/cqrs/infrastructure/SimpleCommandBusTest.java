package org.sanmibuh.cqrs.infrastructure;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.sanmibuh.cqrs.domain.Command;
import org.sanmibuh.cqrs.domain.CommandHandler;
import org.sanmibuh.cqrs.domain.HandlerNotFoundException;

class SimpleCommandBusTest {

  @Test
  void should_dispatchCommand_whenHandlerIsRegistered() {
    final var handler = new StubCommandHandler();
    final var bus = new SimpleCommandBus(List.of(handler));

    bus.dispatch(new StubCommand());

    then(handler.handled).isTrue();
  }

  @Test
  void should_throwHandlerNotFoundException_whenNoHandlerRegistered() {
    final var bus = new SimpleCommandBus(List.of());

    thenThrownBy(() -> bus.dispatch(new StubCommand()))
      .isInstanceOf(HandlerNotFoundException.class)
      .hasMessageContaining(StubCommand.class.getName());
  }

  record StubCommand() implements Command {
  }

  static class StubCommandHandler implements CommandHandler<StubCommand> {

    boolean handled;

    @Override
    public void handle(final StubCommand command) {
      handled = true;
    }
  }
}
