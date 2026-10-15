package org.sanmibuh.ddd.adapter;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.sanmibuh.ddd.port.CommandBus;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

class DddAutoConfigurationTest {

  private final DddAutoConfiguration sut = new DddAutoConfiguration();

  @Test
  void should_returnRegistrar_whenDomainEventHandlerRegistrarCalled() {
    then(DddAutoConfiguration.domainEventHandlerRegistrar(new DefaultListableBeanFactory()))
        .isInstanceOf(DomainEventHandlerRegistrar.class);
  }

  @Test
  void should_returnRegistrar_whenCqrsHandlerRegistrarCalled() {
    then(DddAutoConfiguration.cqrsHandlerRegistrar(new DefaultListableBeanFactory()))
        .isInstanceOf(CQRSHandlerRegistrar.class);
  }

  @Test
  void should_returnEventBus_whenDomainEventHandlersProvided() {
    then(sut.eventBus(List.of())).isInstanceOf(InMemoryEventBus.class);
  }

  @Test
  void should_returnCommandBus_whenCommandHandlersProvided() {
    then(sut.commandBus(List.of(), new InMemoryEventBus(List.of())))
        .isInstanceOf(InMemoryCommandBus.class);
  }

  @Test
  void should_returnTransactionalCommandBus_whenTransactionManagerProvided() {
    try (final var context =
        new AnnotationConfigApplicationContext(TransactionalConfiguration.class)) {
      then(context.getBean(CommandBus.class)).isInstanceOf(TransactionalCommandBus.class);
      then(context.getBean("inMemoryCommandBus", CommandBus.class))
          .isInstanceOf(InMemoryCommandBus.class);
    }
  }

  @Test
  void should_returnQueryBus_whenQueryHandlersProvided() {
    then(sut.queryBus(List.of())).isInstanceOf(InMemoryQueryBus.class);
  }

  @Configuration
  @Import(DddAutoConfiguration.class)
  static class TransactionalConfiguration {

    @Bean
    PlatformTransactionManager transactionManager() {
      return new TestTransactionManager();
    }
  }

  static final class TestTransactionManager extends AbstractPlatformTransactionManager {

    @Override
    protected Object doGetTransaction() {
      return new Object();
    }

    @Override
    protected void doBegin(final Object transaction, final TransactionDefinition definition) {
      // No-op
    }

    @Override
    protected void doCommit(final DefaultTransactionStatus status) {
      // No-op
    }

    @Override
    protected void doRollback(final DefaultTransactionStatus status) {
      // No-op
    }
  }
}
