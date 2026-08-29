package org.sanmibuh.ddd.domain;

import java.util.Optional;

public interface Repository<A extends AggregateRoot<I>, I extends AggregateRootId<?>> {

  Optional<A> findById(I id);

  void save(A aggregate);

  default A get(final I id) {
    return findById(id).orElseThrow(() -> new AggregateNotFoundException(id));
  }
}
