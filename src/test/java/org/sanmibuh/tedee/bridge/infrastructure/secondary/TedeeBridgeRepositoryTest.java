package org.sanmibuh.tedee.bridge.infrastructure.secondary;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.tedee.bridge.domain.vo.BridgeId;
import org.sanmibuh.tedee.lock.infrastructure.secondary.CallbackQueryApi;

@ExtendWith(MockitoExtension.class)
class TedeeBridgeRepositoryTest {

  @Mock CallbackQueryApi callbackQueryApi;

  @InjectMocks TedeeBridgeRepository sut;

  @Test
  void should_returnBridge_whenFindingSingletonBridge() {
    given(callbackQueryApi.getCallbacks()).willReturn(java.util.List.of());

    final var bridge = sut.findById(BridgeId.SINGLETON);

    then(bridge).isPresent();
  }
}
