package org.sanmibuh.tedee.bridge.application;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.port.QueryHandler;
import org.sanmibuh.tedee.bridge.projection.BridgeCallbackQueryPort;

@RequiredArgsConstructor
public final class GetRegisteredCallbacksHandler
    implements QueryHandler<GetRegisteredCallbacksQuery, List<URI>> {

  private final BridgeCallbackQueryPort callbackQueryPort;

  @Override
  public List<URI> handle(final GetRegisteredCallbacksQuery query) {
    return callbackQueryPort.getRegisteredCallbackUrls();
  }
}
