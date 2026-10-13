package org.sanmibuh.tedee.bridge.domain.event;

import java.net.URI;
import org.sanmibuh.ddd.domain.DomainEvent;

public record BridgeCallbackAdded(URI callbackUrl) implements DomainEvent {}
