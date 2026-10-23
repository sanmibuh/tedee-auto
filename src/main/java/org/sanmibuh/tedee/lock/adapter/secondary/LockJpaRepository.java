package org.sanmibuh.tedee.lock.adapter.secondary;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

interface LockJpaRepository extends JpaRepository<LockJpaEntity, Integer> {

  @Override
  @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
  Optional<LockJpaEntity> findById(Integer id);
}
