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

  // Entity identity: equal iff same concrete type and same id, ignoring state and recorded events.
  // Concrete aggregates are final (enforced by ArchUnit), so a distinct class is always a distinct
  // aggregate; getClass() captures that precisely.
  // Hand-written rather than Lombok on purpose: identity is defined once here, in the base, so
  // every
  // aggregate inherits it for free and it cannot be silently forgotten on a new one. Lombok's
  // instanceof/canEqual would only discriminate by concrete type if
  // @EqualsAndHashCode(callSuper=true)
  // were repeated on every leaf; placed on this base it generates canEqual(o) = o instanceof
  // AggregateRoot, making two aggregate types that share an id compare equal.
  @Override
  public final boolean equals(final Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    return id.equals(((AggregateRoot<?>) o).id);
  }

  @Override
  public final int hashCode() {
    return id.hashCode();
  }
}
