package org.sanmibuh.tedee.lock.infrastructure.secondary;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;

class TedeeApiTokenGeneratorTest {

  private final TedeeApiTokenGenerator sut = new TedeeApiTokenGenerator("BE9xnPnGfVUS");

  @Test
  void should_generateEncryptedToken_whenTimestampIsProvided() {
    final var apiToken = sut.generate(1691058833000L);

    then(apiToken)
        .isEqualTo("e59d9763edc6e59f2faccf9a769e5cf170d68439c3fd67afae5e3e72d0463a711691058833000");
  }
}
