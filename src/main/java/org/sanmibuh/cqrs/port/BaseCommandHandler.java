package org.sanmibuh.cqrs.port;

public interface BaseCommandHandler<C extends Command, R> {

  R process(C command);
}
