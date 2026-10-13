package org.sanmibuh.tedee.bridge.application;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;

import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.tedee.bridge.projection.BridgeCallbackQueryPort;

@ExtendWith(MockitoExtension.class)
class GetRegisteredCallbacksHandlerTest {

  @Mock BridgeCallbackQueryPort callbackQueryPort;

  @InjectMocks GetRegisteredCallbacksHandler sut;

  @Test
  void should_returnRegisteredCallbackUrls_whenRetrievingCallbacks() {
    final var callbackUrls = List.of(URI.create("https://automation.local/tedee/events"));
    given(callbackQueryPort.getRegisteredCallbackUrls()).willReturn(callbackUrls);

    final var registeredCallbackUrls = sut.handle(new GetRegisteredCallbacksQuery());

    then(registeredCallbackUrls).containsExactlyElementsOf(callbackUrls);
  }
}
