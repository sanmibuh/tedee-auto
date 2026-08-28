package org.sanmibuh.tedee.lock.infrastructure;

import com.tedee.bridge.client.api.LockApi;
import lombok.RequiredArgsConstructor;
import org.sanmibuh.tedee.lock.domain.LockId;
import org.sanmibuh.tedee.lock.domain.LockPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TedeeApiAdapter implements LockPort {

  private final LockApi lockApi;

  @Override
  public void lock(final LockId lockId) {
    throw new UnsupportedOperationException("not implemented");
  }
}
