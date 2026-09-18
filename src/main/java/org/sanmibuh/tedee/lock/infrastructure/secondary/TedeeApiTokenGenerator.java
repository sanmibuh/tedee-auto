package org.sanmibuh.tedee.lock.infrastructure.secondary;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class TedeeApiTokenGenerator {

  private final String token;

  String generate(final long timestampMillis) {
    throw new UnsupportedOperationException();
  }
}
