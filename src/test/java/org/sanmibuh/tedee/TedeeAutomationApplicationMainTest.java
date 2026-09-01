package org.sanmibuh.tedee;

import static org.mockito.Mockito.mockStatic;

import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;

class TedeeAutomationApplicationMainTest {

  private final Consumer<String[]> sut = TedeeAutomationApplication::main;

  @Test
  void should_delegateToSpringApplication_whenMainIsCalled() {
    final var args = new String[] {};
    try (var mocked = mockStatic(SpringApplication.class)) {
      sut.accept(args);

      mocked.verify(() -> SpringApplication.run(TedeeAutomationApplication.class, args));
    }
  }
}
