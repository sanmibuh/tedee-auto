package org.sanmibuh.tedee.lock.application;

public record ReportLockDisconnectedCommand(int deviceId) implements LockCommand {}
