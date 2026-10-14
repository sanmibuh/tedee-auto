package org.sanmibuh.tedee.bridge.adapter.secondary;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;

import com.tedee.bridge.client.api.CallbackApi;
import com.tedee.bridge.client.model.CallbackDetails;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TedeeCallbackQueryApiTest {

  @Mock CallbackApi callbackApi;

  @InjectMocks TedeeCallbackQueryApi sut;

  @Test
  void should_returnCallbacks_whenRetrievingCallbacks() {
    final var callbacks = List.of(new CallbackDetails());
    given(callbackApi.getCallback()).willReturn(callbacks);

    final var result = sut.getCallbacks();

    then(result).isSameAs(callbacks);
  }
}
