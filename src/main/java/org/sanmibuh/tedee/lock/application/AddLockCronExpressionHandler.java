package org.sanmibuh.tedee.lock.application;

import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.port.CommandHandler;
import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockRepository;
import org.sanmibuh.tedee.lock.domain.vo.CronExpression;
import org.sanmibuh.tedee.lock.domain.vo.LockId;

@RequiredArgsConstructor
public final class AddLockCronExpressionHandler
    extends CommandHandler<AddLockCronExpressionCommand, Lock> {

  private final LockRepository repository;

  @Override
  protected Lock execute(final AddLockCronExpressionCommand command) {
    final var lock = repository.get(new LockId(command.deviceId()));
    lock.addCronExpression(new CronExpression(command.cronExpression()));
    repository.save(lock);

    return lock;
  }
}
