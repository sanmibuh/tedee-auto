package org.sanmibuh.ddd.adapter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.sanmibuh.ddd.port.Command;
import org.sanmibuh.ddd.port.CommandBus;
import org.sanmibuh.ddd.port.TransactionalCommandBus;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.SimpleTransactionStatus;

class TransactionalCommandBusTest {

  @Test
  void should_commitTransaction_whenCommandHandlingSucceeds() {
    try (final var context = new AnnotationConfigApplicationContext(TestConfiguration.class)) {
      final var sut = context.getBean(TransactionalCommandBus.class);
      final var delegate = context.getBean(RecordingCommandBus.class);
      final var transactionManager = context.getBean(RecordingTransactionManager.class);
      final var command = new StubCommand();

      sut.dispatch(command);

      verify(delegate).dispatch(command);
      verify(transactionManager).commit(org.mockito.ArgumentMatchers.any(TransactionStatus.class));
    }
  }

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
    RecordingTransactionManager transactionManager() {
      return mock(RecordingTransactionManager.class);
    }
  }

  interface RecordingCommandBus extends CommandBus {}

  interface RecordingTransactionManager extends PlatformTransactionManager {

    @Override
    default SimpleTransactionStatus getTransaction(
        final @Nullable TransactionDefinition definition) {
      return new SimpleTransactionStatus();
    }

    @Override
    default void commit(final TransactionStatus status) {}

    @Override
    default void rollback(final TransactionStatus status) {}
  }
}
