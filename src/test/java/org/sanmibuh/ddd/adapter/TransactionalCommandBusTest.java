package org.sanmibuh.ddd.adapter;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.sanmibuh.ddd.port.Command;
import org.sanmibuh.ddd.port.CommandBus;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

class TransactionalCommandBusTest {

  @Test
  void should_commitTransaction_whenCommandHandlingSucceeds() {
    try (final var context = new AnnotationConfigApplicationContext(TestConfiguration.class)) {
      final var sut = context.getBean(TransactionalCommandBus.class);
      final var delegate = context.getBean(RecordingCommandBus.class);
      final var transactionManager =
          (RecordingTransactionManager) context.getBean("transactionManager");
      final var command = new StubCommand();

      sut.dispatch(command);

      verify(delegate).dispatch(command);
      then(transactionManager.commitCount).isOne();
    }
  }

  @Test
  void should_rollbackTransaction_whenCommandHandlingFails() {
    try (final var context = new AnnotationConfigApplicationContext(TestConfiguration.class)) {
      final var sut = context.getBean(TransactionalCommandBus.class);
      final var delegate = context.getBean(RecordingCommandBus.class);
      final var transactionManager =
          (RecordingTransactionManager) context.getBean("transactionManager");
      final var command = new StubCommand();
      doThrow(new IllegalStateException()).when(delegate).dispatch(command);

      thenThrownBy(() -> sut.dispatch(command)).isInstanceOf(IllegalStateException.class);

      then(transactionManager.rollbackCount).isOne();
    }
  }

  interface RecordingCommandBus extends CommandBus {}

  record StubCommand() implements Command {}

  @Configuration
  @EnableTransactionManagement
  static class TestConfiguration {

    @Bean
    TransactionalCommandBus commandBus(final RecordingCommandBus delegate) {
      return new InMemoryTransactionalCommandBus(delegate);
    }

    @Bean
    RecordingCommandBus delegate() {
      return mock(RecordingCommandBus.class);
    }

    @Bean
    PlatformTransactionManager transactionManager() {
      return new RecordingTransactionManager();
    }
  }

  static final class RecordingTransactionManager extends AbstractPlatformTransactionManager {

    private int commitCount;
    private int rollbackCount;

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
      commitCount++;
    }

    @Override
    protected void doRollback(final DefaultTransactionStatus status) {
      rollbackCount++;
    }
  }
}
