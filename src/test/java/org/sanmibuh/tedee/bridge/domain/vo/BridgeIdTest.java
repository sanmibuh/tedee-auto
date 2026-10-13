package org.sanmibuh.tedee.bridge.domain.vo;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;

class BridgeIdTest {

  @Test
  void should_returnSingletonValue_whenRetrievingBridgeIdValue() {
    then(BridgeId.SINGLETON.value()).isEqualTo("bridge");
  }
}
