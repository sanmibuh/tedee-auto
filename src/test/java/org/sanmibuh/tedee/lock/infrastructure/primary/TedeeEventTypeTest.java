package org.sanmibuh.tedee.lock.infrastructure.primary;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;

class TedeeEventTypeTest {

  @Test
  void should_returnEmpty_whenEventTypeIsUnknown() {
    then(TedeeEventType.from("unknown-event")).isEmpty();
  }

  @Test
  void should_returnLockStatusChanged_whenEventTypeIsKnown() {
    then(TedeeEventType.from("lock-status-changed")).contains(TedeeEventType.LOCK_STATUS_CHANGED);
  }
}
