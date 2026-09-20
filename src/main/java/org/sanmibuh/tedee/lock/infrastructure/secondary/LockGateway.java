package org.sanmibuh.tedee.lock.infrastructure.secondary;

import org.sanmibuh.tedee.lock.domain.LockId;
import org.sanmibuh.tedee.lock.domain.LockTemporarilyUnavailableException;
import org.springframework.resilience.annotation.Retryable;

interface LockGateway {

  @Retryable(
      includes = LockTemporarilyUnavailableException.class,
      maxRetriesString = "${sanmibuh.rest.tedee.retry.max-retries}",
      delayString = "${sanmibuh.rest.tedee.retry.initial-interval}",
      multiplierString = "${sanmibuh.rest.tedee.retry.multiplier}",
      maxDelayString = "${sanmibuh.rest.tedee.retry.max-interval}")
  void lock(LockId lockId);
}
