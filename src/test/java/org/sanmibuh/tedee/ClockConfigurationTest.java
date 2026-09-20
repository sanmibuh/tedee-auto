package org.sanmibuh.tedee;

import static org.assertj.core.api.BDDAssertions.then;

import java.time.Clock;
import org.junit.jupiter.api.Test;

class ClockConfigurationTest {

  private final ClockConfiguration sut = new ClockConfiguration();

  @Test
  void should_provideSystemUtcClock_whenClockIsRequested() {
    then(sut.clock()).isEqualTo(Clock.systemUTC());
  }
}
