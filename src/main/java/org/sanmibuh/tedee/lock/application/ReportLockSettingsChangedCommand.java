package org.sanmibuh.tedee.lock.application;

public record ReportLockSettingsChangedCommand(int deviceId) implements LockCommand {}
