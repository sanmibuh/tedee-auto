package org.sanmibuh.tedee.lock.application;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockRepository;
import org.sanmibuh.tedee.lock.domain.event.BatteryLevelReported;
import org.sanmibuh.tedee.lock.domain.event.BatteryLevelReported.BatteryStatus;
import org.sanmibuh.tedee.lock.domain.vo.BatteryLevelCategory;
import org.sanmibuh.tedee.lock.domain.vo.LockId;
import org.sanmibuh.tedee.lock.domain.vo.LockStatus;

@ExtendWith(MockitoExtension.class)
class ReportBatteryLevelHandlerTest {

  @Mock LockRepository repository;

  @InjectMocks ReportBatteryLevelHandler sut;

  @Captor ArgumentCaptor<Lock> savedLock;

  @ParameterizedTest
  @ValueSource(ints = {0, 42, 100})
  void should_recordBatteryLevelReported_whenReportingLevel(final int level) {
    given(repository.get(new LockId(1))).willReturn(new Lock(new LockId(1), LockStatus.UNLOCKED));

    final var events = sut.handle(new ReportBatteryLevelCommand(1, level));

    verify(repository).save(savedLock.capture());
    then(savedLock.getValue().domainEvents())
        .containsExactly(
            new BatteryLevelReported(
                1, new BatteryStatus(level, BatteryLevelCategory.from(level))));
    then(events)
        .containsExactly(
            new BatteryLevelReported(
                1, new BatteryStatus(level, BatteryLevelCategory.from(level))));
  }
}
