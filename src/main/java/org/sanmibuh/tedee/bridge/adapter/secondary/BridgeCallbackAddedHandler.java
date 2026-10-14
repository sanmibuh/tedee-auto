package org.sanmibuh.tedee.bridge.adapter.secondary;

import static org.sanmibuh.tedee.shared.adapter.TedeeInfrastructureConfiguration.CALLBACK_SECRET_HEADER;

import com.tedee.bridge.client.model.CallbackDetailsNoId;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.port.DomainEventHandler;
import org.sanmibuh.tedee.bridge.domain.event.BridgeCallbackAdded;
import org.sanmibuh.tedee.shared.adapter.TedeeProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class BridgeCallbackAddedHandler implements DomainEventHandler<BridgeCallbackAdded> {

  private final CallbackCommandApi callbackCommandApi;
  private final TedeeProperties tedeeProperties;

  @Override
  public void handle(final BridgeCallbackAdded event) {
    callbackCommandApi.createCallback(
        new CallbackDetailsNoId()
            .url(event.callbackUrl().toString())
            .method(HttpMethod.POST.name())
            .addHeadersItem(Map.of(CALLBACK_SECRET_HEADER, tedeeProperties.callbackSecret())));
  }
}
