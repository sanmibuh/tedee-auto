package org.sanmibuh.tedee.lock.application;

import org.sanmibuh.ddd.port.Command;

public record ReportBatteryLevelCommand(int deviceId, int level) implements Command {}
