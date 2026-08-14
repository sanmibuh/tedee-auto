package org.sanmibuh.cqrs.domain;

public interface CommandHandler<C extends Command> {

  void handle(C command);
}
