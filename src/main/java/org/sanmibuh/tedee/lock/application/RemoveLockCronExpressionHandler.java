package org.sanmibuh.tedee.lock.application;

import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.port.CommandHandler;
import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockRepository;
import org.sanmibuh.tedee.lock.domain.vo.CronExpression;
import org.sanmibuh.tedee.lock.domain.vo.LockId;

@RequiredArgsConstructor
public final class RemoveLockCronExpressionHandler
    extends CommandHandler<RemoveLockCronExpressionCommand, Lock> {

  private final LockRepository repository;

  @Override
  protected Lock execute(final RemoveLockCronExpressionCommand command) {
    final var lock = repository.get(new LockId(command.deviceId()));
    lock.removeCronExpression(new CronExpression(command.cronExpression()));
    repository.save(lock);

    return lock;
  }
}
