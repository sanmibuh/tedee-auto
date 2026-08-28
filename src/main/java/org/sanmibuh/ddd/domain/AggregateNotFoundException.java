package org.sanmibuh.ddd.domain;

public class AggregateNotFoundException extends DomainException {

  public AggregateNotFoundException(final Class<?> aggregateType, final Object id) {
    super(aggregateType.getSimpleName() + " not found: " + id);
  }
}
