package org.sanmibuh.cqrs.infrastructure;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.sanmibuh.cqrs.domain.CommandBus;
import org.sanmibuh.cqrs.domain.QueryBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.StubCommandHandler;
import com.example.StubCommandHandler.StubCommand;
import com.example.StubQueryHandler.StubQuery;
import com.example.TestApplication;

@SpringBootTest(
  classes = TestApplication.class)
class CQRSAutoConfigurationTest {

  @Autowired
  private CommandBus commandBus;

  @Autowired
  private QueryBus queryBus;

  @Autowired
  private StubCommandHandler commandHandler;

  @Test
  void should_dispatchCommand_whenHandlerIsInAutoConfigurationPackage() {
    commandBus.dispatch(new StubCommand());

    then(commandHandler.handled).isTrue();
  }

  @Test
  void should_executeQuery_whenHandlerIsInAutoConfigurationPackage() {
    final var result = queryBus.dispatch(new StubQuery());

    then(result).isEqualTo("stub-result");
  }
}
