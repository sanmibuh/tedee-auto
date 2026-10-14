package org.sanmibuh.tedee.bridge.application;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.tedee.bridge.domain.Bridge;
import org.sanmibuh.tedee.bridge.domain.BridgeRepository;
import org.sanmibuh.tedee.bridge.domain.BridgeSnapshot;
import org.sanmibuh.tedee.bridge.domain.event.BridgeCallbackAdded;
import org.sanmibuh.tedee.bridge.domain.vo.BridgeId;

@ExtendWith(MockitoExtension.class)
class AddBridgeCallbackHandlerTest {

  @Mock BridgeRepository repository;

  @InjectMocks AddBridgeCallbackHandler sut;

  @Captor ArgumentCaptor<Bridge> savedBridge;

  @Test
  void should_recordCallbackAdded_whenAddingMissingCallback() {
    final var callbackUrl = URI.create("https://automation.local/tedee/events");
    given(repository.get(BridgeId.SINGLETON)).willReturn(Bridge.create());

    sut.handle(new AddBridgeCallbackCommand(callbackUrl));

    verify(repository).save(savedBridge.capture());
    then(savedBridge.getValue().domainEvents())
        .containsExactly(new BridgeCallbackAdded(callbackUrl));
  }

  @Test
  void should_notRecordEvent_whenAddingExistingCallback() {
    final var callbackUrl = URI.create("https://automation.local/tedee/events");
    given(repository.get(BridgeId.SINGLETON))
        .willReturn(new Bridge(new BridgeSnapshot(List.of(callbackUrl))));

    sut.handle(new AddBridgeCallbackCommand(callbackUrl));

    verify(repository).save(savedBridge.capture());
    then(savedBridge.getValue().domainEvents()).isEmpty();
  }
}
