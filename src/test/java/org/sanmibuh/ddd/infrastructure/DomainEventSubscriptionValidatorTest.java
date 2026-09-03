package org.sanmibuh.ddd.infrastructure;

import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.sanmibuh.ddd.domain.DomainEvent;

class DomainEventSubscriptionValidatorTest {

  private final DomainEventSubscriptionValidator sut = new DomainEventSubscriptionValidator();

  @Test
  void should_throwException_whenPublishableEventHasNoHandler() {
    thenThrownBy(() -> sut.validate(List.of(UnhandledEvent.class), Set.of()))
        .isInstanceOf(MissingEventHandlerException.class)
        .hasMessageContaining(UnhandledEvent.class.getName());
  }

  record UnhandledEvent() implements DomainEvent {}
}
