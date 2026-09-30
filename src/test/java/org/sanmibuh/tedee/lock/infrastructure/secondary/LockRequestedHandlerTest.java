package org.sanmibuh.tedee.lock.infrastructure.secondary;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.tedee.lock.domain.LockId;
import org.sanmibuh.tedee.lock.domain.LockRequested;

@ExtendWith(MockitoExtension.class)
class LockRequestedHandlerTest {

  @Mock LockGateway lockGateway;

  @InjectMocks LockRequestedHandler sut;

  @Test
  void should_lockTheDevice_whenHandlingLockRequested() {
    sut.handle(new LockRequested(42));

    verify(lockGateway).lock(new LockId(42));
  }
}
