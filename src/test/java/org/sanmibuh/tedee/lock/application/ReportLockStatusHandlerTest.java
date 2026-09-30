package org.sanmibuh.tedee.lock.application;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockId;
import org.sanmibuh.tedee.lock.domain.LockRepository;
import org.sanmibuh.tedee.lock.domain.LockState;
import org.sanmibuh.tedee.lock.domain.LockStateChanged;
import org.sanmibuh.tedee.lock.domain.LockStatus;

@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
class ReportLockStatusHandlerTest {

  @Mock LockRepository repository;

  @InjectMocks ReportLockStatusHandler sut;

  @Captor ArgumentCaptor<Lock> savedLock;

  @InjectSoftAssertions BDDSoftAssertions softly;

  @Test
  void should_recordLockStateChangedAndReturnEvents_whenReportingClosedState() {
    given(repository.get(new LockId(1))).willReturn(new Lock(new LockId(1), LockStatus.UNLOCKED));

    final var actual = sut.handle(new ReportLockStatusCommand(1, 6, 0, 2));

    verify(repository).save(savedLock.capture());
    softly
        .then(savedLock.getValue().domainEvents())
        .containsExactly(new LockStateChanged(1, LockState.CLOSED));
    softly.then(actual).containsExactly(new LockStateChanged(1, LockState.CLOSED));
  }
}
