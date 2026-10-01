package org.sanmibuh.tedee.lock.application;

import static org.junit.jupiter.params.provider.Arguments.argumentSet;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.stream.Stream;
import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockId;
import org.sanmibuh.tedee.lock.domain.LockJamStatus;
import org.sanmibuh.tedee.lock.domain.LockRepository;
import org.sanmibuh.tedee.lock.domain.LockState;
import org.sanmibuh.tedee.lock.domain.LockStatus;
import org.sanmibuh.tedee.lock.domain.LockStatusReported;

@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
class ReportLockStatusHandlerTest {

  @Mock LockRepository repository;

  @InjectMocks ReportLockStatusHandler sut;

  @Captor ArgumentCaptor<Lock> savedLock;

  @InjectSoftAssertions BDDSoftAssertions softly;

  static Stream<Arguments> reportedStates() {
    return Stream.of(
        argumentSet("uncalibrated", 0, LockState.UNCALIBRATED),
        argumentSet("calibration", 1, LockState.CALIBRATION),
        argumentSet("open", 2, LockState.OPEN),
        argumentSet("partially open", 3, LockState.PARTIALLY_OPEN),
        argumentSet("opening", 4, LockState.OPENING),
        argumentSet("closing", 5, LockState.CLOSING),
        argumentSet("closed", 6, LockState.CLOSED),
        argumentSet("pull spring", 7, LockState.PULL_SPRING),
        argumentSet("pulling", 8, LockState.PULLING),
        argumentSet("unknown", 9, LockState.UNKNOWN),
        argumentSet("undocumented state", 10, LockState.UNKNOWN),
        argumentSet("unpulling", 255, LockState.UNPULLING));
  }

  @ParameterizedTest
  @MethodSource("reportedStates")
  void should_recordLockStatusReportedAndReturnEvents_whenReportingState(
      final int bridgeState, final LockState state) {
    given(repository.get(new LockId(1))).willReturn(new Lock(new LockId(1), LockStatus.UNLOCKED));

    final var actual = sut.handle(new ReportLockStatusCommand(1, bridgeState, 0, 2));

    verify(repository).save(savedLock.capture());
    softly
        .then(savedLock.getValue().domainEvents())
        .containsExactly(new LockStatusReported(1, state, LockJamStatus.NOT_JAMMED));
    softly.then(actual).containsExactly(new LockStatusReported(1, state, LockJamStatus.NOT_JAMMED));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1})
  void should_recordLockStatusReported_whenReportingJammedState(final int jammed) {
    given(repository.get(new LockId(1))).willReturn(new Lock(new LockId(1), LockStatus.UNLOCKED));

    sut.handle(new ReportLockStatusCommand(1, 6, jammed, 2));

    verify(repository).save(savedLock.capture());
    softly
        .then(savedLock.getValue().domainEvents())
        .containsExactly(
            new LockStatusReported(
                1,
                LockState.CLOSED,
                jammed == 1 ? LockJamStatus.JAMMED : LockJamStatus.NOT_JAMMED));
  }
}
