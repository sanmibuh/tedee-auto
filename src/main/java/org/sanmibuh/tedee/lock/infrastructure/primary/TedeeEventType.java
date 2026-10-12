package org.sanmibuh.tedee.lock.infrastructure.primary;

import java.util.Optional;

enum TedeeEventType {
  LOCK_STATUS_CHANGED("lock-status-changed");

  private final String event;

  TedeeEventType(final String event) {
    this.event = event;
  }

  static Optional<TedeeEventType> from(final String event) {
    for (final var type : values()) {
      if (type.event.equals(event)) {
        return Optional.of(type);
      }
    }
    return Optional.empty();
  }
}
