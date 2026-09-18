package org.sanmibuh.tedee.lock.infrastructure.secondary;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
final class TedeeApiTokenGenerator {

  private static final String HASH_ALGORITHM = "SHA-256";

  private final String token;

  String generate(final long timestampMillis) {
    return sha256Hex(token + timestampMillis) + timestampMillis;
  }

  @SneakyThrows
  private String sha256Hex(final String value) {
    final var digest =
        MessageDigest.getInstance(HASH_ALGORITHM).digest(value.getBytes(StandardCharsets.UTF_8));
    return HexFormat.of().formatHex(digest);
  }
}
