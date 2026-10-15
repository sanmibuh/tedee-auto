package org.sanmibuh.ddd.adapter;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.mock;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.sanmibuh.ddd.port.CommandBus;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

class DddAutoConfigurationTest {

  private final DddAutoConfiguration sut = new DddAutoConfiguration();
  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(DddAutoConfiguration.class));

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
    then(sut.inMemoryCommandBus(List.of(), new InMemoryEventBus(List.of())))
        .isInstanceOf(InMemoryCommandBus.class);
  }

  @Test
  void should_returnTransactionalCommandBus_whenTransactionManagerProvided() {
    contextRunner
        .withUserConfiguration(TransactionalConfiguration.class)
        .run(
            context -> {
              then(context.getBean(CommandBus.class)).isInstanceOf(TransactionalCommandBus.class);
              then(context.getBean("inMemoryCommandBus", CommandBus.class))
                  .isInstanceOf(InMemoryCommandBus.class);
            });
  }

  @Test
  void should_notRegisterDefaultCommandBuses_whenCustomCommandBusProvided() {
    contextRunner
        .withUserConfiguration(CustomCommandBusConfiguration.class)
        .run(
            context -> {
              then(context).doesNotHaveBean(InMemoryCommandBus.class);
              then(context).doesNotHaveBean(TransactionalCommandBus.class);
            });
  }

  @Test
  void should_returnQueryBus_whenQueryHandlersProvided() {
    then(sut.queryBus(List.of())).isInstanceOf(InMemoryQueryBus.class);
  }

  @Configuration
  static class TransactionalConfiguration {

    @Bean
    PlatformTransactionManager transactionManager() {
      return new TestTransactionManager();
    }
  }

  @Configuration
  static class CustomCommandBusConfiguration {

    @Bean
    CommandBus commandBus() {
      return mock(CommandBus.class);
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
