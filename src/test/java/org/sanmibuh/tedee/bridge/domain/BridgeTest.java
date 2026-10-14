package org.sanmibuh.tedee.bridge.domain;

import static org.assertj.core.api.BDDAssertions.then;

import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.sanmibuh.tedee.bridge.domain.vo.BridgeId;

class BridgeTest {

  @Test
  void should_returnSnapshot_whenReconstructingBridge() {
    final var snapshot =
        new BridgeSnapshot(
            BridgeId.SINGLETON.value(),
            List.of(URI.create("https://automation.local/tedee/events")));
    final var sut = new Bridge(snapshot);

    then(sut.snapshot()).isEqualTo(snapshot);
  }
}
