package org.sanmibuh.tedee.lock.application;

import org.sanmibuh.ddd.port.Command;

public record AddLockCronExpressionCommand(int deviceId, String cronExpression)
    implements Command {}
