package org.sanmibuh.tedee.lock.application;

import org.sanmibuh.cqrs.api.Command;

public record CloseLockCommand(int deviceId) implements Command {}
