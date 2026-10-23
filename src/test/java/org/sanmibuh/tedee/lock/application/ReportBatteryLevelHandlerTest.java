package org.sanmibuh.tedee.lock.application;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.params.provider.Arguments.argumentSet;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.sanmibuh.tedee.lock.LockSnapshotMother.ID;
import static org.sanmibuh.tedee.lock.LockSnapshotMother.NAME;
import static org.sanmibuh.tedee.lock.LockSnapshotMother.snapshot;

import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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

  static Stream<Arguments> batteryLevels() {
    return Stream.of(
        argumentSet("depleted minimum", 0, BatteryLevelCategory.DEPLETED),
        argumentSet("depleted maximum", 10, BatteryLevelCategory.DEPLETED),
        argumentSet("critical minimum", 11, BatteryLevelCategory.CRITICAL),
        argumentSet("critical maximum", 20, BatteryLevelCategory.CRITICAL),
        argumentSet("low minimum", 21, BatteryLevelCategory.LOW),
        argumentSet("low maximum", 40, BatteryLevelCategory.LOW),
        argumentSet("medium minimum", 41, BatteryLevelCategory.MEDIUM),
        argumentSet("medium maximum", 60, BatteryLevelCategory.MEDIUM),
        argumentSet("good minimum", 61, BatteryLevelCategory.GOOD),
        argumentSet("good maximum", 80, BatteryLevelCategory.GOOD),
        argumentSet("high minimum", 81, BatteryLevelCategory.HIGH),
        argumentSet("high maximum", 100, BatteryLevelCategory.HIGH));
  }

  @ParameterizedTest
  @MethodSource("batteryLevels")
  void should_recordBatteryLevelReported_whenReportingLevel(
      final int level, final BatteryLevelCategory category) {
    given(repository.get(new LockId(ID))).willReturn(new Lock(snapshot(LockStatus.UNLOCKED)));

    sut.handle(new ReportBatteryLevelCommand(ID, level));

    verify(repository).save(savedLock.capture());
    then(savedLock.getValue().domainEvents())
        .containsExactly(new BatteryLevelReported(ID, NAME, new BatteryStatus(level, category)));
  }
}
