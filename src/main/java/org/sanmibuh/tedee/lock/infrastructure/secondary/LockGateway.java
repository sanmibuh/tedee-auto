package org.sanmibuh.tedee.lock.infrastructure.secondary;

import org.sanmibuh.tedee.lock.domain.LockId;

interface LockGateway {

  void lock(LockId lockId);
}
