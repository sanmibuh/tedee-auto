package org.sanmibuh.tedee.lock.application;

import org.sanmibuh.ddd.port.Command;

public record ReportLockConnectedCommand(int deviceId) implements Command {}
