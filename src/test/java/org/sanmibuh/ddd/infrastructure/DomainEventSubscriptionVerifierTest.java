package org.sanmibuh.ddd.infrastructure;

import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;

class DomainEventSubscriptionVerifierTest {

  @Test
  void should_throwException_whenScannedEventHasNoHandler() {
    final var beanFactory = new DefaultListableBeanFactory();
    AutoConfigurationPackages.register(beanFactory, "com.example");
    final var sut = new DomainEventSubscriptionVerifier(beanFactory, List.of());

    thenThrownBy(sut::afterPropertiesSet).isInstanceOf(MissingEventHandlerException.class);
  }
}
