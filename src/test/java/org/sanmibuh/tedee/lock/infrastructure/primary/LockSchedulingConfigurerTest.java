package org.sanmibuh.tedee.lock.infrastructure.primary;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.verify;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@ExtendWith(MockitoExtension.class)
class LockSchedulingConfigurerTest {

  private static final int DEVICE_ID = 12345;
  private static final String CRON = "0 0 0,22 * * *";

  @Mock LockScheduler scheduler;

  @Test
  void should_registerCronTaskWithConfiguredExpression_whenConfiguringTasks() {
    final var properties = new LockSchedulerProperties(Map.of(DEVICE_ID, CRON));
    final var sut = new LockSchedulingConfigurer(properties, scheduler);
    final var registrar = new ScheduledTaskRegistrar();

    sut.configureTasks(registrar);

    then(registrar.getCronTaskList())
        .singleElement()
        .extracting(CronTask::getExpression)
        .isEqualTo(CRON);
  }

  @Test
  void should_closeConfiguredLock_whenCronTaskRuns() {
    final var properties = new LockSchedulerProperties(Map.of(DEVICE_ID, CRON));
    final var sut = new LockSchedulingConfigurer(properties, scheduler);
    final var registrar = new ScheduledTaskRegistrar();
    sut.configureTasks(registrar);

    registrar.getCronTaskList().getFirst().getRunnable().run();

    verify(scheduler).closeLock(DEVICE_ID);
  }
}
