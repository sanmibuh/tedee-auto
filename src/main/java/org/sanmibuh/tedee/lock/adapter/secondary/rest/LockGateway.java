package org.sanmibuh.tedee.lock.adapter.secondary.rest;

import org.sanmibuh.tedee.lock.domain.exception.LockTemporarilyUnavailableException;
import org.sanmibuh.tedee.lock.domain.vo.LockId;
import org.springframework.resilience.annotation.Retryable;

@Retryable(
    includes = LockTemporarilyUnavailableException.class,
    maxRetriesString = "${sanmibuh.rest.tedee.retry.max-retries}",
    delayString = "${sanmibuh.rest.tedee.retry.initial-interval}",
    multiplierString = "${sanmibuh.rest.tedee.retry.multiplier}",
    maxDelayString = "${sanmibuh.rest.tedee.retry.max-interval}")
public interface LockGateway {

  void lock(LockId lockId);

  LockView get(LockId lockId);
}
