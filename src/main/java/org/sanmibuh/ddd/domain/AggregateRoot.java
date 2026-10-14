package org.sanmibuh.ddd.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AggregateRoot<I extends AggregateRootId<T>, T, S extends EntitySnapshot<T>>
    extends Entity<I, T, S> {

  private final List<DomainEvent> domainEvents = new ArrayList<>();

  protected AggregateRoot(final I id) {
    super(id);
  }

  protected final void recordEvent(final DomainEvent event) {
    domainEvents.add(event);
  }

  public final List<DomainEvent> domainEvents() {
    return Collections.unmodifiableList(domainEvents);
  }
}
