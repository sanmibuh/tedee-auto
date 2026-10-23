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
import org.sanmibuh.tedee.lock.domain.event.LockDisconnected;
import org.sanmibuh.tedee.lock.domain.vo.LockId;
import org.sanmibuh.tedee.lock.domain.vo.LockState;

@ExtendWith(MockitoExtension.class)
class ReportLockDisconnectedHandlerTest {

  @Mock LockRepository repository;

  @InjectMocks ReportLockDisconnectedHandler sut;

  @Captor ArgumentCaptor<Lock> savedLock;

  @Test
  void should_recordLockDisconnected_whenReportingDisconnectedLock() {
    given(repository.get(new LockId(ID))).willReturn(new Lock(snapshot(LockState.OPEN)));

    sut.handle(new ReportLockDisconnectedCommand(1));

    verify(repository).save(savedLock.capture());
    then(savedLock.getValue().domainEvents()).containsExactly(new LockDisconnected(ID, NAME));
  }
}
