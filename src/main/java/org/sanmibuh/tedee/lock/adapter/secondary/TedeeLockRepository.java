package org.sanmibuh.tedee.lock.adapter.secondary;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockRepository;
import org.sanmibuh.tedee.lock.domain.LockSnapshot;
import org.sanmibuh.tedee.lock.domain.vo.LockId;
import org.sanmibuh.tedee.lock.domain.vo.LockStatus;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public final class TedeeLockRepository implements LockRepository {

  private final LockJpaRepository jpaRepository;
  private final LockGateway lockGateway;

  @Override
  public Optional<Lock> findById(final LockId lockId) {
    final List<String> cronExpressions =
        jpaRepository.findById(lockId.value()).map(LockJpaEntity::getCronExpressions).stream()
            .flatMap(Set::stream)
            .toList();
    return Optional.of(
        new Lock(new LockSnapshot(lockId.value(), "", LockStatus.UNLOCKED, cronExpressions)));
  }

  @Override
  public void save(final LockSnapshot snapshot) {
    // The lock status is not persisted: the physical lock is authoritative and a stale LOCKED
    // value would turn later scheduled closes into no-ops.
    final var entity =
        jpaRepository.findById(snapshot.id()).orElseGet(() -> new LockJpaEntity(snapshot.id()));
    entity.update(snapshot);
    jpaRepository.save(entity);
  }
}
