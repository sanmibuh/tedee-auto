package com.example.verifier;

import org.sanmibuh.ddd.domain.DomainEvent;
import org.sanmibuh.ddd.domain.NoSubscribersRequired;

@NoSubscribersRequired
public record OptedOutStubEvent() implements DomainEvent {}
