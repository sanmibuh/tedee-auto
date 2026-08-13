package org.sanmibuh.tedee.automation.lock.domain;

/**
 * Lock identifier value object.
 */
public record LockId(int value) {

    public LockId {
        if (value <= 0) {
            throw new IllegalArgumentException("Lock ID must be positive");
        }
    }
}
