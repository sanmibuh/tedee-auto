package org.sanmibuh.tedee.automation.lock.infrastructure;

import org.sanmibuh.tedee.automation.lock.domain.Lock;
import org.sanmibuh.tedee.automation.lock.domain.LockId;
import org.sanmibuh.tedee.automation.lock.domain.LockState;
import org.sanmibuh.tedee.automation.lock.domain.TedeeApiPort;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Adapter that calls the Tedee Cloud API.
 */
@Component
public class TedeeApiAdapter implements TedeeApiPort {

    private final RestClient restClient;

    public TedeeApiAdapter(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://api.tedee.com")
                .build();
    }

    @Override
    public Lock fetchLock(LockId id) {
        // TODO: implement real API call and deserialisation
        return new Lock(id, "Unknown", LockState.UNKNOWN);
    }

    @Override
    public void lock(LockId id) {
        // TODO: implement POST /api/v1.32/my/lock/{id}/operation/lock
    }

    @Override
    public void unlock(LockId id) {
        // TODO: implement POST /api/v1.32/my/lock/{id}/operation/unlock
    }
}
