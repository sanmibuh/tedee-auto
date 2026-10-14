package org.sanmibuh.tedee.bridge.infrastructure.primary;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenCode;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

import java.net.URI;
import nl.altindag.log.LogCaptor;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.ddd.port.CommandBus;
import org.sanmibuh.tedee.ServerProperties;
import org.sanmibuh.tedee.bridge.application.AddBridgeCallbackCommand;
import org.springframework.web.client.RestClientException;

@ExtendWith(MockitoExtension.class)
class TedeeCallbackRegistrationTest {

  @Mock CommandBus commandBus;

  @Test
  void should_dispatchAddBridgeCallback_whenStarting() {
    final var callbackUrl = URI.create("https://automation.local/tedee/events");
    final var sut =
        new TedeeCallbackRegistration(commandBus, new ServerProperties("https://automation.local"));

    sut.start();

    verify(commandBus).dispatch(new AddBridgeCallbackCommand(callbackUrl));
  }

  @Test
  void should_logWarningWithoutStopping_whenCallbackRegistrationFails() {
    final var callbackUrl = URI.create("https://automation.local/tedee/events");
    willThrow(new RestClientException("Bridge unavailable"))
        .given(commandBus)
        .dispatch(new AddBridgeCallbackCommand(callbackUrl));
    final var sut =
        new TedeeCallbackRegistration(commandBus, new ServerProperties("https://automation.local"));

    try (final var logCaptor = LogCaptor.forClass(TedeeCallbackRegistration.class)) {
      thenCode(sut::start).doesNotThrowAnyException();
      then(logCaptor.getWarnLogs())
          .singleElement(InstanceOfAssertFactories.STRING)
          .contains(callbackUrl.toString());
    }
  }
}
