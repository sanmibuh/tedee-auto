package org.sanmibuh.tedee.lock.infrastructure.secondary;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;

class TedeeApiTokenGeneratorTest {

  private final TedeeApiTokenGenerator sut = new TedeeApiTokenGenerator("FAKE_TEST_TOKEN");

  @Test
  void should_generateEncryptedToken_whenTimestampIsProvided() {
    final var apiToken = sut.generate(1691058833000L);

    then(apiToken)
        .isEqualTo("244ff2dbcb627d504cc85876098ed4e2de89acafbee8fdd6442e5ba2cebdb1501691058833000");
  }
}
