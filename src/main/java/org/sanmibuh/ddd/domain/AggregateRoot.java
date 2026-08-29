package org.sanmibuh.ddd.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AggregateRoot<I extends AggregateRootId<?>> {

  private final I id;
  private final List<DomainEvent> domainEvents = new ArrayList<>();

  protected AggregateRoot(final I id) {
    this.id = id;
  }

  public final I id() {
    return id;
  }

  protected final void recordEvent(final DomainEvent event) {
    domainEvents.add(event);
  }

  public final List<DomainEvent> domainEvents() {
    return Collections.unmodifiableList(domainEvents);
  }
}
