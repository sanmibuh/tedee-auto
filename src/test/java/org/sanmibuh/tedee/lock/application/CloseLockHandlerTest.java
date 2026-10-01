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
import org.sanmibuh.tedee.lock.domain.LockRepository;
import org.sanmibuh.tedee.lock.domain.event.LockRequested;
import org.sanmibuh.tedee.lock.domain.vo.LockId;
import org.sanmibuh.tedee.lock.domain.vo.LockStatus;

@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
class CloseLockHandlerTest {

  @Mock LockRepository repository;

  @InjectMocks CloseLockHandler sut;

  @Captor ArgumentCaptor<Lock> savedLock;

  @InjectSoftAssertions BDDSoftAssertions softly;

  @Test
  void should_recordLockRequestedAndReturnEvents_whenClosingAnOpenLock() {
    given(repository.get(new LockId(1))).willReturn(new Lock(new LockId(1), LockStatus.UNLOCKED));

    final var actual = sut.handle(new CloseLockCommand(1));

    verify(repository).save(savedLock.capture());
    softly.then(savedLock.getValue().domainEvents()).containsExactly(new LockRequested(1));
    softly.then(actual).containsExactly(new LockRequested(1));
  }

  @Test
  void should_notRecordEventAndReturnNoEvents_whenClosingAnAlreadyClosedLock() {
    given(repository.get(new LockId(1))).willReturn(new Lock(new LockId(1), LockStatus.LOCKED));

    final var actual = sut.handle(new CloseLockCommand(1));

    verify(repository).save(savedLock.capture());
    softly.then(savedLock.getValue().domainEvents()).isEmpty();
    softly.then(actual).isEmpty();
  }
}
