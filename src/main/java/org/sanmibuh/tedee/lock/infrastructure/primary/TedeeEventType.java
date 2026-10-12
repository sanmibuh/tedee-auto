package org.sanmibuh.tedee.lock.infrastructure.primary;

import java.util.Optional;

enum TedeeEventType {
  LOCK_STATUS_CHANGED;

  static Optional<TedeeEventType> from(final String event) {
    return Optional.empty();
  }
}
