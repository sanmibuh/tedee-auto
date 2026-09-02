package org.sanmibuh.ddd.port;

import java.util.Optional;
import org.sanmibuh.ddd.domain.AggregateNotFoundException;
import org.sanmibuh.ddd.domain.AggregateRoot;
import org.sanmibuh.ddd.domain.AggregateRootId;

public interface Repository<A extends AggregateRoot<I>, I extends AggregateRootId<?>> {

  Optional<A> findById(I id);

  void save(A aggregate);

  default A get(final I id) {
    return findById(id).orElseThrow(() -> new AggregateNotFoundException(id));
  }
}
