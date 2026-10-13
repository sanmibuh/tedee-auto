package org.sanmibuh.tedee.bridge.projection;

import java.net.URI;
import java.util.List;

public interface BridgeCallbackQueryPort {

  List<URI> getRegisteredCallbackUrls();
}
