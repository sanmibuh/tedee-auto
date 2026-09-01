package org.sanmibuh.tedee.lock.application;

import org.sanmibuh.cqrs.port.Command;

public record CloseLockCommand(int deviceId) implements Command {}
