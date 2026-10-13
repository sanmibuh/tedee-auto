package org.sanmibuh.tedee.bridge.infrastructure.secondary;

import static org.mockito.Mockito.verify;

import java.net.URI;
import java.util.Map;
import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.tedee.bridge.domain.event.BridgeCallbackAdded;
import org.sanmibuh.tedee.lock.infrastructure.TedeeProperties;
import org.sanmibuh.tedee.lock.infrastructure.secondary.CallbackCommandApi;

@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
class BridgeCallbackAddedHandlerTest {

  private static final String CALLBACK_SECRET = "callback-secret";

  @Mock CallbackCommandApi callbackCommandApi;

  @Mock TedeeProperties tedeeProperties;

  @InjectMocks BridgeCallbackAddedHandler sut;

  @Captor ArgumentCaptor<com.tedee.bridge.client.model.CallbackDetailsNoId> callback;

  @InjectSoftAssertions BDDSoftAssertions softly;

  @Test
  void should_createCallbackWithSecret_whenHandlingCallbackAdded() {
    final var callbackUrl = URI.create("https://automation.local/tedee/events");

    sut.handle(new BridgeCallbackAdded(callbackUrl));

    verify(callbackCommandApi).createCallback(callback.capture());
    final var registeredCallback = callback.getValue();
    softly.then(registeredCallback.getUrl()).isEqualTo(callbackUrl.toString());
    softly.then(registeredCallback.getMethod()).isEqualTo("POST");
    softly
        .then(registeredCallback.getHeaders())
        .containsExactly(Map.of("X-Tedee-Callback-Secret", CALLBACK_SECRET));
  }
}
