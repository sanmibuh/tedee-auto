package org.sanmibuh.tedee.lock.domain;

import org.sanmibuh.ddd.domain.AggregateRoot;
import org.sanmibuh.tedee.lock.domain.event.BatteryLevelReported;
import org.sanmibuh.tedee.lock.domain.event.BatteryLevelReported.BatteryStatus;
import org.sanmibuh.tedee.lock.domain.event.LockConnected;
import org.sanmibuh.tedee.lock.domain.event.LockDisconnected;
import org.sanmibuh.tedee.lock.domain.event.LockRequested;
import org.sanmibuh.tedee.lock.domain.event.LockSettingsChanged;
import org.sanmibuh.tedee.lock.domain.event.LockStatusReported;
import org.sanmibuh.tedee.lock.domain.vo.BatteryIndicator;
import org.sanmibuh.tedee.lock.domain.vo.LockId;
import org.sanmibuh.tedee.lock.domain.vo.LockJamStatus;
import org.sanmibuh.tedee.lock.domain.vo.LockState;
import org.sanmibuh.tedee.lock.domain.vo.LockStatus;

public final class Lock extends AggregateRoot<LockId, LockSnapshot> {

  private LockStatus status;

  public Lock(final LockSnapshot snapshot) {
    super(new LockId(snapshot.id()));
    status = snapshot.status();
  }

  public static Lock create(final LockId id, final LockStatus status) {
    return new Lock(new LockSnapshot(id.value(), status));
  }

  @Override
  public LockSnapshot snapshot() {
    return new LockSnapshot(id().value(), status);
  }

  public void lock() {
    if (status == LockStatus.LOCKED) {
      return;
    }
    status = LockStatus.LOCKED;
    recordEvent(new LockRequested(id().value()));
  }

  public void reportStatus(final LockState state, final LockJamStatus jamStatus) {
    recordEvent(new LockStatusReported(id().value(), state, jamStatus));
  }

  public void reportConnected() {
    recordEvent(new LockConnected(id().value()));
  }

  public void reportDisconnected() {
    recordEvent(new LockDisconnected(id().value()));
  }

  public void reportSettingsChanged() {
    recordEvent(new LockSettingsChanged(id().value()));
  }

  public void reportBatteryLevel(final BatteryIndicator indicator) {
    recordEvent(
        new BatteryLevelReported(
            id().value(),
            new BatteryStatus(indicator.getLevel().value(), indicator.getCategory())));
  }
}
