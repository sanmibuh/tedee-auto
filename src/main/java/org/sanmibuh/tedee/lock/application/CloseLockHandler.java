package org.sanmibuh.tedee.lock.application;

import lombok.RequiredArgsConstructor;
import org.sanmibuh.cqrs.api.CommandHandler;
import org.sanmibuh.tedee.lock.domain.LockId;
import org.sanmibuh.tedee.lock.domain.LockRepository;

@RequiredArgsConstructor
public class CloseLockHandler implements CommandHandler<CloseLockCommand> {

  private final LockRepository repository;

  @Override
  public void handle(final CloseLockCommand command) {
    final var lock = repository.get(new LockId(command.deviceId()));
    lock.lock();
    repository.save(lock);
  }
}
