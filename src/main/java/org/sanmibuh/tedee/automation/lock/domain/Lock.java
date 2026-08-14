package org.sanmibuh.tedee.automation.lock.domain;

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
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public LockState getState() {
		return this.state;
	}

	public void updateState(LockState newState) {
		this.state = newState;
	}
}
