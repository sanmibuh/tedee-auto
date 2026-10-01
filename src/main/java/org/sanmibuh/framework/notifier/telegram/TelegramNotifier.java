package org.sanmibuh.framework.notifier.telegram;

import lombok.RequiredArgsConstructor;
import org.sanmibuh.framework.notifier.Notifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
final class TelegramNotifier implements Notifier {

  private final RestClient.Builder builder;
  private final TelegramProperties properties;

  @Override
  public void notify(final String message) {
    throw new UnsupportedOperationException();
  }
}
