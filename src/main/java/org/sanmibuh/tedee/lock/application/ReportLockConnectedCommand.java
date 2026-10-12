package org.sanmibuh.tedee.lock.application;

public record ReportLockConnectedCommand(int deviceId) implements LockCommand {}
