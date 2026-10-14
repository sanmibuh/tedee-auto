package org.sanmibuh.tedee.bridge.application;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.util.List;
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
import org.sanmibuh.tedee.bridge.domain.Bridge;
import org.sanmibuh.tedee.bridge.domain.BridgeRepository;
import org.sanmibuh.tedee.bridge.domain.BridgeSnapshot;
import org.sanmibuh.tedee.bridge.domain.event.BridgeCallbackAdded;
import org.sanmibuh.tedee.bridge.domain.vo.BridgeId;

@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
class AddBridgeCallbackHandlerTest {

  @Mock BridgeRepository repository;

  @InjectMocks AddBridgeCallbackHandler sut;

  @Captor ArgumentCaptor<Bridge> savedBridge;

  @InjectSoftAssertions BDDSoftAssertions softly;

  @Test
  void should_recordCallbackAdded_whenAddingMissingCallback() {
    final var callbackUrl = URI.create("https://automation.local/tedee/events");
    given(repository.get(BridgeId.SINGLETON)).willReturn(Bridge.create());

    sut.handle(new AddBridgeCallbackCommand(callbackUrl));

    verify(repository).save(savedBridge.capture());

    final var actual = savedBridge.getValue();
    var expectedEvent = new BridgeCallbackAdded(callbackUrl);
    softly.then(actual.domainEvents()).containsExactly(expectedEvent);
    final var expectedSnapshot =
        new BridgeSnapshot(BridgeId.SINGLETON.value(), List.of(callbackUrl));
    softly.then(actual.snapshot()).isEqualTo(expectedSnapshot);
  }

  @Test
  void should_notRecordEvent_whenAddingExistingCallback() {
    final var callbackUrl = URI.create("https://automation.local/tedee/events");
    given(repository.get(BridgeId.SINGLETON))
        .willReturn(
            new Bridge(new BridgeSnapshot(BridgeId.SINGLETON.value(), List.of(callbackUrl))));

    sut.handle(new AddBridgeCallbackCommand(callbackUrl));

    verify(repository).save(savedBridge.capture());
    then(savedBridge.getValue().domainEvents()).isEmpty();
  }
}
