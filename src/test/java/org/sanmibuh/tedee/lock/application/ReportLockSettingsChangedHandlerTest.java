package org.sanmibuh.tedee.lock.application;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockRepository;
import org.sanmibuh.tedee.lock.domain.event.LockSettingsChanged;
import org.sanmibuh.tedee.lock.domain.vo.LockId;
import org.sanmibuh.tedee.lock.domain.vo.LockStatus;

@ExtendWith(MockitoExtension.class)
class ReportLockSettingsChangedHandlerTest {

  @Mock LockRepository repository;

  @InjectMocks ReportLockSettingsChangedHandler sut;

  @Captor ArgumentCaptor<Lock> savedLock;

  @Test
  void should_recordLockSettingsChanged_whenReportingLockSettingsChanged() {
    given(repository.get(new LockId(1))).willReturn(new Lock(new LockId(1), LockStatus.UNLOCKED));

    sut.handle(new ReportLockSettingsChangedCommand(1));

    verify(repository).save(savedLock.capture());
    then(savedLock.getValue().domainEvents()).containsExactly(new LockSettingsChanged(1));
  }
}
