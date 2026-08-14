package org.sanmibuh.tedee.automation.lock.domain;

public record LockId(int value) {

    public LockId {
        if (value <= 0) {
            throw new InvalidLockIdException(value);
        }
    }
}
