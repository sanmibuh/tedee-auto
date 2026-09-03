package org.sanmibuh.ddd.infrastructure;

import static org.assertj.core.api.BDDAssertions.then;

import com.example.StubDomainEvent;
import java.util.List;
import org.junit.jupiter.api.Test;

class DomainEventScannerTest {

  private final DomainEventScanner sut = new DomainEventScanner();

  @Test
  void should_findDomainEventTypes_whenPresentInPackage() {
    final var actual = sut.scan(List.of("com.example"));

    then(actual).contains(StubDomainEvent.class);
  }
}
