package org.sanmibuh.tedee;

import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;

class TedeeAutomationApplicationMainTest {

  @Test
  void should_delegateToSpringApplication_whenMainIsCalled() {
    final var args = new String[] {};
    try (var mocked = mockStatic(SpringApplication.class)) {
      TedeeAutomationApplication.main(args);

      mocked.verify(() -> SpringApplication.run(TedeeAutomationApplication.class, args));
    }
  }
}
