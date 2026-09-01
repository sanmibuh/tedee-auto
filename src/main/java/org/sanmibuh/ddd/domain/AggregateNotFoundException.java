package org.sanmibuh.ddd.domain;

public final class AggregateNotFoundException extends DomainException {

  public AggregateNotFoundException(final AggregateRootId<?> id) {
    super(id.getClass().getSimpleName() + " not found: " + id.value());
  }
}
