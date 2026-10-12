package org.sanmibuh.tedee.lock.application;

import org.sanmibuh.ddd.port.Command;

public record ReportLockSettingsChangedCommand(int deviceId) implements Command {}
