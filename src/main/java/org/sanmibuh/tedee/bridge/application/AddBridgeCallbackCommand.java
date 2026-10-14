package org.sanmibuh.tedee.bridge.application;

import java.net.URI;
import org.sanmibuh.ddd.port.Command;

public record AddBridgeCallbackCommand(URI callbackUrl) implements Command {}
