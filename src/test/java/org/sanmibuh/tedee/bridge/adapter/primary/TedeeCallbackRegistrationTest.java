package org.sanmibuh.tedee.bridge.adapter.primary;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenCode;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.net.URI;
import nl.altindag.log.LogCaptor;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.ddd.port.CommandBus;
import org.sanmibuh.tedee.ServerProperties;
import org.sanmibuh.tedee.bridge.application.AddBridgeCallbackCommand;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.client.RestClientException;

@ExtendWith(MockitoExtension.class)
class TedeeCallbackRegistrationTest {

  private static final URI CALLBACK_URL = URI.create("https://automation.local/tedee/events");

  @Mock CommandBus commandBus;

  TedeeCallbackRegistration sut;

  @BeforeEach
  void createSut() {
    sut =
        new TedeeCallbackRegistration(commandBus, new ServerProperties("https://automation.local"));
  }

  private ApplicationContextRunner runner() {
    return new ApplicationContextRunner()
        .withBean(CommandBus.class, () -> commandBus)
        .withBean(ServerProperties.class, () -> new ServerProperties("https://automation.local"))
        .withBean(TedeeCallbackRegistration.class);
  }

  @Test
  void should_dispatchAddBridgeCallback_whenStarting() {
    sut.start();

    verify(commandBus).dispatch(new AddBridgeCallbackCommand(CALLBACK_URL));
  }

  @Test
  void should_logWarningWithoutStopping_whenCallbackRegistrationFails() {
    willThrow(new RestClientException("Bridge unavailable"))
        .given(commandBus)
        .dispatch(new AddBridgeCallbackCommand(CALLBACK_URL));
    try (final var logCaptor = LogCaptor.forClass(TedeeCallbackRegistration.class)) {
      thenCode(sut::start).doesNotThrowAnyException();
      then(logCaptor.getWarnLogs())
          .singleElement(InstanceOfAssertFactories.STRING)
          .contains(CALLBACK_URL.toString());
    }
  }

  @Test
  void should_registerCallbackOnce_whenContextStartsAndIsStartedAgain() {
    runner()
        .run(
            context -> {
              context.start();

              verify(commandBus, times(1)).dispatch(new AddBridgeCallbackCommand(CALLBACK_URL));
            });
  }

  @Test
  void should_registerCallbackAgain_whenContextIsRestarted() {
    runner()
        .run(
            context -> {
              context.stop();
              context.start();

              verify(commandBus, times(2)).dispatch(new AddBridgeCallbackCommand(CALLBACK_URL));
            });
  }
}
