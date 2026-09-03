package org.sanmibuh.ddd.infrastructure;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.port.DomainEventHandler;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;

@RequiredArgsConstructor(access = lombok.AccessLevel.PACKAGE)
final class DomainEventSubscriptionVerifier implements InitializingBean {

  private final BeanFactory beanFactory;
  private final List<DomainEventHandler<?>> handlers;

  @Override
  public void afterPropertiesSet() {
    if (!AutoConfigurationPackages.has(beanFactory)) {
      return;
    }
    final var packages = AutoConfigurationPackages.get(beanFactory);
    final var publishableEvents = new DomainEventScanner().scan(packages);
    final var handledEventTypes = new EventHandlerRegistry(handlers).handledEventTypes();
    new DomainEventSubscriptionValidator().validate(publishableEvents, handledEventTypes);
  }
}
