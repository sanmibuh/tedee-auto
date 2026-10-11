package org.sanmibuh.tedee.lock.application;

import org.sanmibuh.ddd.port.Command;

public record ReportLockDisconnectedCommand(int deviceId) implements Command {}
