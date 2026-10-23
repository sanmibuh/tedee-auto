package org.sanmibuh.tedee.lock.adapter.secondary.rest;

import org.sanmibuh.tedee.lock.domain.vo.LockState;

public record LockView(int id, String name, LockState state) {}
