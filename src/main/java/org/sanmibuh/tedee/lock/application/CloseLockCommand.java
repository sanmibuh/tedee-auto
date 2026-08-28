package org.sanmibuh.tedee.lock.application;

import org.sanmibuh.cqrs.domain.Command;

public record CloseLockCommand(int deviceId) implements Command {}
