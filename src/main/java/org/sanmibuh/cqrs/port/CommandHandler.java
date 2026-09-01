package org.sanmibuh.cqrs.port;

public abstract class CommandHandler<C extends Command> implements BaseCommandHandler<C, Void> {

  protected abstract void execute(C command);

  @Override
  public final Void handle(final C command) {
    execute(command);
    return null;
  }
}
