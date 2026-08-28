package org.sanmibuh.tedee.lock.application;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.ddd.domain.AggregateNotFoundException;
import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockId;
import org.sanmibuh.tedee.lock.domain.LockLocked;
import org.sanmibuh.tedee.lock.domain.LockPort;
import org.sanmibuh.tedee.lock.domain.LockStatus;

@ExtendWith(MockitoExtension.class)
class CloseLockHandlerTest {

  @Mock LockPort lockPort;

  @InjectMocks CloseLockHandler sut;

  @Captor ArgumentCaptor<Lock> savedLock;

  @Test
  void should_recordLockLocked_whenClosingAnOpenLock() {
    given(lockPort.findById(new LockId(1)))
        .willReturn(Optional.of(new Lock(new LockId(1), LockStatus.UNLOCKED)));

    sut.handle(new CloseLockCommand(1));

    verify(lockPort).save(savedLock.capture());
    then(savedLock.getValue().domainEvents()).containsExactly(new LockLocked(new LockId(1)));
  }

  @Test
  void should_notRecordEvent_whenClosingAnAlreadyClosedLock() {
    given(lockPort.findById(new LockId(1)))
        .willReturn(Optional.of(new Lock(new LockId(1), LockStatus.LOCKED)));

    sut.handle(new CloseLockCommand(1));

    verify(lockPort).save(savedLock.capture());
    then(savedLock.getValue().domainEvents()).isEmpty();
  }

  @Test
  void should_throwAggregateNotFoundException_whenLockDoesNotExist() {
    given(lockPort.findById(new LockId(1))).willReturn(Optional.empty());

    thenThrownBy(() -> sut.handle(new CloseLockCommand(1)))
        .isInstanceOf(AggregateNotFoundException.class);
  }
}
