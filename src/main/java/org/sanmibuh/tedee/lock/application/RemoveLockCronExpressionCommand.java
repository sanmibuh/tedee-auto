package org.sanmibuh.tedee.lock.application;

import org.sanmibuh.ddd.port.Command;

public record RemoveLockCronExpressionCommand(int deviceId, String cronExpression)
    implements Command {}
