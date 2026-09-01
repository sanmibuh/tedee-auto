package org.sanmibuh.cqrs.port;

public interface CommandHandler<C extends Command> extends BaseCommandHandler<C, Void> {

  void handle(C command);

  @Override
  default Void process(C command) {
    handle(command);
    return null;
  }
}
