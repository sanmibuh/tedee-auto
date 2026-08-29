package org.sanmibuh.tedee.lock.infrastructure.primary;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.verify;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
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

  private final ScheduledTaskRegistrar registrar = new ScheduledTaskRegistrar();

  @BeforeEach
  void configureTasks() {
    final var properties = new LockSchedulerProperties(Map.of(DEVICE_ID, CRON));
    new LockSchedulingConfigurer(properties, scheduler).configureTasks(registrar);
  }

  @Test
  void should_registerCronTaskWithConfiguredExpression_whenConfiguringTasks() {
    then(registrar.getCronTaskList())
        .singleElement()
        .extracting(CronTask::getExpression)
        .isEqualTo(CRON);
  }

  @Test
  void should_closeConfiguredLock_whenCronTaskRuns() {
    registrar.getCronTaskList().getFirst().getRunnable().run();

    verify(scheduler).closeLock(DEVICE_ID);
  }
}
