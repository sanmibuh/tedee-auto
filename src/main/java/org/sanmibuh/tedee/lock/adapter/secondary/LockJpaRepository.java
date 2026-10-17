package org.sanmibuh.tedee.lock.adapter.secondary;

import org.springframework.data.jpa.repository.JpaRepository;

interface LockJpaRepository extends JpaRepository<LockJpaEntity, Integer> {}
