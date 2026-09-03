package org.sanmibuh.ddd.infrastructure;

import static org.assertj.core.api.BDDAssertions.thenNoException;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.sanmibuh.ddd.domain.DomainEvent;
import org.sanmibuh.ddd.domain.NoSubscribersRequired;

class DomainEventSubscriptionValidatorTest {

  private final DomainEventSubscriptionValidator sut = new DomainEventSubscriptionValidator();

  @Test
  void should_throwException_whenPublishableEventHasNoHandler() {
    thenThrownBy(() -> sut.validate(List.of(UnhandledEvent.class), Set.of()))
        .isInstanceOf(MissingEventHandlerException.class)
        .hasMessageContaining(UnhandledEvent.class.getName());
  }

  @Test
  void should_notThrowException_whenPublishableEventOptsOutOfSubscribers() {
    thenNoException().isThrownBy(() -> sut.validate(List.of(OptedOutEvent.class), Set.of()));
  }

  record UnhandledEvent() implements DomainEvent {}

  @NoSubscribersRequired
  record OptedOutEvent() implements DomainEvent {}
}
