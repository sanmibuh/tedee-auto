package org.sanmibuh.tedee.lock.infrastructure;

import com.tedee.bridge.client.api.LockApi;
import lombok.RequiredArgsConstructor;
import org.sanmibuh.tedee.lock.domain.InvalidApiTokenException;
import org.sanmibuh.tedee.lock.domain.LockId;
import org.sanmibuh.tedee.lock.domain.LockNotFoundException;
import org.sanmibuh.tedee.lock.domain.LockPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
@RequiredArgsConstructor
public class TedeeApiAdapter implements LockPort {

  private final LockApi lockApi;

  @Override
  public void lock(final LockId lockId) {
    try {
      lockApi.postLock(lockId.deviceId());
    } catch (final HttpClientErrorException exception) {
      if (exception.getStatusCode() == HttpStatus.UNAUTHORIZED) {
        throw new InvalidApiTokenException();
      }
      if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new LockNotFoundException(lockId.deviceId());
      }
      throw exception;
    }
  }
}
