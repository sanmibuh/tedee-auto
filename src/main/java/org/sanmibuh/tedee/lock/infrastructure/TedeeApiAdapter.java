package org.sanmibuh.tedee.lock.infrastructure;

import org.sanmibuh.tedee.lock.domain.LockId;
import org.sanmibuh.tedee.lock.domain.LockPort;
import org.springframework.stereotype.Component;

@Component
public class TedeeApiAdapter implements LockPort {

  @Override
  public void lock(final LockId lockId) {
    throw new UnsupportedOperationException("not implemented");
  }
}
