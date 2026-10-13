package org.sanmibuh.tedee.bridge.application;

import java.net.URI;
import java.util.List;
import org.sanmibuh.ddd.port.QueryHandler;

public final class GetRegisteredCallbacksHandler
    implements QueryHandler<GetRegisteredCallbacksQuery, List<URI>> {

  @Override
  public List<URI> handle(final GetRegisteredCallbacksQuery query) {
    return List.of();
  }
}
