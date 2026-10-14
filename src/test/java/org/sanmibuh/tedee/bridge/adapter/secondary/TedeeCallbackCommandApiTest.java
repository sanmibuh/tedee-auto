package org.sanmibuh.tedee.bridge.adapter.secondary;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;

import com.tedee.bridge.client.api.CallbackApi;
import com.tedee.bridge.client.model.CallbackDetailsNoId;
import com.tedee.bridge.client.model.CallbackIDsingle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TedeeCallbackCommandApiTest {

  @Mock CallbackApi callbackApi;

  @InjectMocks TedeeCallbackCommandApi sut;

  @Test
  void should_returnCreatedCallback_whenCreatingCallback() {
    final var callback = new CallbackDetailsNoId();
    final var createdCallback = new CallbackIDsingle();
    given(callbackApi.postSingleCallback(callback)).willReturn(createdCallback);

    final var result = sut.createCallback(callback);

    then(result).isSameAs(createdCallback);
  }
}
