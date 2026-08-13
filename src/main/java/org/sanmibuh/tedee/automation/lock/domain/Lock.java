package org.sanmibuh.tedee.automation.lock.domain;

/**
 * Lock aggregate root.
 */
public class Lock {

    private final LockId id;
    private final String name;
    private LockState state;

    public Lock(LockId id, String name, LockState state) {
        this.id = id;
        this.name = name;
        this.state = state;
    }

    public LockId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LockState getState() {
        return state;
    }

    public void updateState(LockState newState) {
        this.state = newState;
    }
}
