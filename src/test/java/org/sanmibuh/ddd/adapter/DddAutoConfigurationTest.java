package org.sanmibuh.ddd.adapter;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.mock;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.sanmibuh.ddd.domain.AggregateRoot;
import org.sanmibuh.ddd.domain.AggregateRootId;
import org.sanmibuh.ddd.domain.EntitySnapshot;
import org.sanmibuh.ddd.port.Command;
import org.sanmibuh.ddd.port.CommandBus;
import org.sanmibuh.ddd.port.CommandHandler;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
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
  void should_registerInMemoryCommandBus_whenCustomCommandBusMissing() {
    contextRunner.run(
        context -> then(context.getBean(CommandBus.class)).isInstanceOf(InMemoryCommandBus.class));
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
  void should_start_whenCustomCommandBusAndTransactionManagerProvided() {
    contextRunner
        .withUserConfiguration(
            CustomCommandBusConfiguration.class, TransactionalConfiguration.class)
        .run(context -> then(context).hasNotFailed());
  }

  @Test
  void should_notRegisterTransactionalCommandBus_whenCustomCommandBusUsesInMemoryName() {
    contextRunner
        .withUserConfiguration(
            SameNameCustomCommandBusConfiguration.class, TransactionalConfiguration.class)
        .run(
            context -> {
              then(context).doesNotHaveBean(TransactionalCommandBus.class);
              then(context.getBean(CommandBus.class))
                  .isSameAs(context.getBean("inMemoryCommandBus"));
            });
  }

  @Test
  void should_startTransaction_whenCommandDispatched() {
    contextRunner
        .withUserConfiguration(TransactionalConfiguration.class, CommandHandlerConfiguration.class)
        .run(
            context -> {
              var transactionManager = context.getBean(TestTransactionManager.class);

              context.getBean(CommandBus.class).dispatch(new TestCommand());

              then(transactionManager.transactionBegun).isTrue();
            });
  }

  @Test
  void should_returnTransactionalCommandBus_whenTransactionManagerAutoConfiguredAfterDdd() {
    new ApplicationContextRunner()
        .withConfiguration(
            AutoConfigurations.of(
                DddAutoConfiguration.class, LateTransactionManagerAutoConfiguration.class))
        .run(
            context ->
                then(context.getBean(CommandBus.class))
                    .isInstanceOf(TransactionalCommandBus.class));
  }

  @Test
  void should_startTransaction_whenTransactionManagerAutoConfiguredAfterDdd() {
    new ApplicationContextRunner()
        .withConfiguration(
            AutoConfigurations.of(
                DddAutoConfiguration.class, LateTransactionManagerAutoConfiguration.class))
        .withUserConfiguration(CommandHandlerConfiguration.class)
        .run(
            context -> {
              var transactionManager = context.getBean(TestTransactionManager.class);

              context.getBean(CommandBus.class).dispatch(new TestCommand());

              then(transactionManager.transactionBegun).isTrue();
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

  @Configuration
  static class SameNameCustomCommandBusConfiguration {

    @Bean("inMemoryCommandBus")
    CommandBus commandBus() {
      return mock(CommandBus.class);
    }
  }

  @Configuration
  static class CommandHandlerConfiguration {

    @Bean
    CommandHandler<TestCommand, TestAggregate> commandHandler() {
      return new TestCommandHandler();
    }
  }

  @AutoConfiguration
  @AutoConfigureAfter(DddAutoConfiguration.class)
  static class LateTransactionManagerAutoConfiguration {

    @Bean
    PlatformTransactionManager lateTransactionManager() {
      return new TestTransactionManager();
    }
  }

  static final class TestTransactionManager extends AbstractPlatformTransactionManager {

    private boolean transactionBegun;

    @Override
    protected Object doGetTransaction() {
      return new Object();
    }

    @Override
    protected void doBegin(final Object transaction, final TransactionDefinition definition) {
      transactionBegun = true;
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

  record TestCommand() implements Command {}

  record TestAggregateId(Integer value) implements AggregateRootId<Integer> {}

  record TestAggregateSnapshot(Integer id) implements EntitySnapshot<Integer> {}

  static final class TestAggregate
      extends AggregateRoot<TestAggregateId, Integer, TestAggregateSnapshot> {

    TestAggregate() {
      super(new TestAggregateId(1));
    }

    @Override
    public TestAggregateSnapshot snapshot() {
      return new TestAggregateSnapshot(id().value());
    }
  }

  static final class TestCommandHandler extends CommandHandler<TestCommand, TestAggregate> {

    @Override
    protected TestAggregate execute(final TestCommand command) {
      return new TestAggregate();
    }
  }
}
