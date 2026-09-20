package org.sanmibuh.tedee.lock.infrastructure.primary;

import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class LockSchedulingConfigurer implements SchedulingConfigurer {

  private final LockSchedulerProperties properties;
  private final LockScheduler scheduler;

  @Override
  public void configureTasks(final ScheduledTaskRegistrar registrar) {
    final var zone = ZoneId.of(properties.zone());
    properties
        .schedules()
        .forEach(
            (deviceId, cron) ->
                registrar.addCronTask(
                    new CronTask(
                        () -> scheduler.closeLock(deviceId), new CronTrigger(cron, zone))));
  }
}
