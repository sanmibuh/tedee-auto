package org.sanmibuh.tedee.lock.domain;

import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LockIdTest {

  @ParameterizedTest
  @ValueSource(ints = {0, -1, -100})
  void should_throwException_whenDeviceIdIsNotPositive(final int deviceId) {
    thenThrownBy(() -> new LockId(deviceId)).isInstanceOf(InvalidLockIdException.class);
  }
}
