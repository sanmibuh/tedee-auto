package org.sanmibuh.tedee.automation.lock.infrastructure;

import org.sanmibuh.tedee.automation.lock.domain.Lock;
import org.sanmibuh.tedee.automation.lock.domain.LockId;
import org.sanmibuh.tedee.automation.lock.domain.LockRepository;
import org.sanmibuh.tedee.automation.lock.domain.LockState;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of LockRepository (replace with persistent store as needed).
 */
@Repository
public class InMemoryLockRepository implements LockRepository {

    private final Map<Integer, Lock> store = new ConcurrentHashMap<>();

    @Override
    public void save(Lock lock) {
        store.put(lock.getId().value(), lock);
    }

    @Override
    public Optional<Lock> findById(LockId id) {
        return Optional.ofNullable(store.get(id.value()));
    }
}
