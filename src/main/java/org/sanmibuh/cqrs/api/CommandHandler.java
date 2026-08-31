package org.sanmibuh.cqrs.api;

public interface CommandHandler<C extends Command> {

  void handle(C command);
}
