package org.sanmibuh.ddd.infrastructure;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.port.DomainEventHandler;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;

@RequiredArgsConstructor(access = lombok.AccessLevel.PACKAGE)
final class DomainEventSubscriptionVerifier implements InitializingBean {

  private final BeanFactory beanFactory;
  private final List<DomainEventHandler<?>> handlers;

  @Override
  public void afterPropertiesSet() {}
}
