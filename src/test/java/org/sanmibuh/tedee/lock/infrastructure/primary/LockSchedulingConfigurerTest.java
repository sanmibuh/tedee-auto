package org.sanmibuh.tedee.lock.infrastructure.primary;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.verify;

import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@ExtendWith(MockitoExtension.class)
class LockSchedulingConfigurerTest {

  private static final int DEVICE_ID = 12345;
  private static final int OTHER_DEVICE_ID = 67890;
  private static final String CRON = "0 0 0,22 * * *";
  private static final String OTHER_CRON = "0 30 6 * * *";

  @Mock LockScheduler scheduler;

  private final ScheduledTaskRegistrar registrar = new ScheduledTaskRegistrar();

  @ParameterizedTest
  @MethodSource("schedules")
  void should_registerOneCronTaskPerConfiguredSchedule_whenConfiguringTasks(
      final Map<Integer, String> schedules, final String[] expectedExpressions) {
    configureTasks(schedules);

    then(registrar.getCronTaskList())
        .extracting(CronTask::getExpression)
        .containsExactlyInAnyOrder(expectedExpressions);
  }

  private static Stream<Arguments> schedules() {
    return Stream.of(
        Arguments.of(Named.of("no schedules", Map.of()), new String[] {}),
        Arguments.of(Named.of("a single schedule", Map.of(DEVICE_ID, CRON)), new String[] {CRON}),
        Arguments.of(
            Named.of("multiple schedules", Map.of(DEVICE_ID, CRON, OTHER_DEVICE_ID, OTHER_CRON)),
            new String[] {CRON, OTHER_CRON}));
  }

  @Test
  void should_closeConfiguredLock_whenCronTaskRuns() {
    configureTasks(Map.of(DEVICE_ID, CRON));

    registrar.getCronTaskList().getFirst().getRunnable().run();

    verify(scheduler).closeLock(DEVICE_ID);
  }

  private void configureTasks(final Map<Integer, String> schedules) {
    final var properties = new LockSchedulerProperties(schedules);
    final var sut = new LockSchedulingConfigurer(properties, scheduler);
    sut.configureTasks(registrar);
  }
}
