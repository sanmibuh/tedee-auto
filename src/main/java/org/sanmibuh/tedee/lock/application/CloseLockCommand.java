package org.sanmibuh.tedee.lock.application;

import org.sanmibuh.ddd.port.Command;

public record CloseLockCommand(int deviceId) implements Command {}
