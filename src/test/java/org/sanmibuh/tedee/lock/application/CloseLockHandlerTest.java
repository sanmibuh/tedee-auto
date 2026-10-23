package org.sanmibuh.tedee.lock.application;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.sanmibuh.tedee.lock.LockSnapshotMother.ID;
import static org.sanmibuh.tedee.lock.LockSnapshotMother.NAME;
import static org.sanmibuh.tedee.lock.LockSnapshotMother.snapshot;

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

@ExtendWith(MockitoExtension.class)
class CloseLockHandlerTest {

  @Mock LockRepository repository;

  @InjectMocks CloseLockHandler sut;

  @Captor ArgumentCaptor<Lock> savedLock;

  @Test
  void should_recordLockRequested_whenClosingAnOpenLock() {
    given(repository.get(new LockId(ID))).willReturn(new Lock(snapshot(LockStatus.UNLOCKED)));

    sut.handle(new CloseLockCommand(1));

    verify(repository).save(savedLock.capture());
    then(savedLock.getValue().domainEvents()).containsExactly(new LockRequested(ID, NAME));
  }

  @Test
  void should_notRecordEvent_whenClosingAnAlreadyClosedLock() {
    given(repository.get(new LockId(ID))).willReturn(new Lock(snapshot(LockStatus.LOCKED)));

    sut.handle(new CloseLockCommand(1));

    verify(repository).save(savedLock.capture());
    then(savedLock.getValue().domainEvents()).isEmpty();
  }
}
