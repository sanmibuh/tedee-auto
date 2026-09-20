package org.sanmibuh.tedee.lock.infrastructure.secondary;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.tedee.lock.domain.LockId;
import org.sanmibuh.tedee.lock.domain.LockLocked;

@ExtendWith(MockitoExtension.class)
class LockLockedHandlerTest {

  @Mock LockGateway lockGateway;

  @InjectMocks LockLockedHandler sut;

  @Test
  void should_lockTheDevice_whenHandlingLockLocked() {
    sut.handle(new LockLocked(42));

    verify(lockGateway).lock(new LockId(42));
  }
}
