package org.sanmibuh.tedee.lock.application;

import org.sanmibuh.ddd.port.Command;

public record ReportLockStatusCommand(int deviceId, int state, boolean jammed, int doorState)
    implements Command {}
