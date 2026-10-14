package org.sanmibuh.tedee.bridge.domain;

import static org.assertj.core.api.BDDAssertions.then;

import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.sanmibuh.tedee.bridge.domain.vo.BridgeId;

class BridgeSnapshotTest {

  @Test
  void should_returnUnmodifiableCallbacks_whenExposingSnapshot() {
    final var snapshot =
        new BridgeSnapshot(
            BridgeId.SINGLETON.value(),
            List.of(URI.create("https://automation.local/tedee/events")));

    then(snapshot.callbacks()).isUnmodifiable();
  }
}
