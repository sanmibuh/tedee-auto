package org.sanmibuh.tedee.bridge.infrastructure.secondary;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;

import com.tedee.bridge.client.model.CallbackDetails;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.tedee.lock.infrastructure.secondary.CallbackQueryApi;

@ExtendWith(MockitoExtension.class)
class TedeeBridgeCallbackProjectionTest {

  @Mock CallbackQueryApi callbackQueryApi;

  @InjectMocks TedeeBridgeCallbackProjection sut;

  @Test
  void should_returnCallbackUrls_whenRetrievingRegisteredCallbacks() {
    final var callbackUrl = "https://automation.local/tedee/events";
    given(callbackQueryApi.getCallbacks())
        .willReturn(List.of(new CallbackDetails().url(callbackUrl)));

    final var callbackUrls = sut.getRegisteredCallbackUrls();

    then(callbackUrls).containsExactly(URI.create(callbackUrl));
  }
}
