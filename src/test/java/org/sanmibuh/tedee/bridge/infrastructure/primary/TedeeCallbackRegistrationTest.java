package org.sanmibuh.tedee.bridge.infrastructure.primary;

import static org.mockito.Mockito.verify;

import java.net.URI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.ddd.port.CommandBus;
import org.sanmibuh.tedee.ServerProperties;
import org.sanmibuh.tedee.bridge.application.AddBridgeCallbackCommand;

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
}
