package org.sanmibuh.tedee.lock.domain.vo;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class LockStateTest {

  @ParameterizedTest
  @EnumSource(value = LockState.class, mode = EnumSource.Mode.EXCLUDE, names = "CLOSED")
  void should_notBeLocked_whenStateIsNotClosed(final LockState sut) {
    then(sut.isLocked()).isFalse();
  }

  @ParameterizedTest
  @EnumSource(value = LockState.class, names = "CLOSED")
  void should_beLocked_whenStateIsClosed(final LockState sut) {
    then(sut.isLocked()).isTrue();
  }
}
