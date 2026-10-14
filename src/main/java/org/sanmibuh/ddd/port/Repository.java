package org.sanmibuh.ddd.port;

import java.util.Optional;
import org.sanmibuh.ddd.domain.AggregateNotFoundException;
import org.sanmibuh.ddd.domain.AggregateRoot;
import org.sanmibuh.ddd.domain.AggregateRootId;
import org.sanmibuh.ddd.domain.Snapshot;

public interface Repository<
    A extends AggregateRoot<I, S>, I extends AggregateRootId<?>, S extends Snapshot> {

  Optional<A> findById(I id);

  void save(S snapshot);

  default void save(final A aggregate) {
    save(aggregate.snapshot());
  }

  default A get(final I id) {
    return findById(id).orElseThrow(() -> new AggregateNotFoundException(id));
  }
}
