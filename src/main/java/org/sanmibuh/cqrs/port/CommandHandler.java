package org.sanmibuh.cqrs.port;

public interface CommandHandler<C extends Command> {

  void handle(C command);
}
