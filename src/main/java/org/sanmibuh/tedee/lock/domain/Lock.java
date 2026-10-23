package org.sanmibuh.tedee.lock.domain;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.sanmibuh.ddd.domain.AggregateRoot;
import org.sanmibuh.tedee.lock.domain.event.BatteryLevelReported;
import org.sanmibuh.tedee.lock.domain.event.BatteryLevelReported.BatteryStatus;
import org.sanmibuh.tedee.lock.domain.event.LockConnected;
import org.sanmibuh.tedee.lock.domain.event.LockDisconnected;
import org.sanmibuh.tedee.lock.domain.event.LockRequested;
import org.sanmibuh.tedee.lock.domain.event.LockSettingsChanged;
import org.sanmibuh.tedee.lock.domain.event.LockStatusReported;
import org.sanmibuh.tedee.lock.domain.vo.BatteryIndicator;
import org.sanmibuh.tedee.lock.domain.vo.CronExpression;
import org.sanmibuh.tedee.lock.domain.vo.LockId;
import org.sanmibuh.tedee.lock.domain.vo.LockJamStatus;
import org.sanmibuh.tedee.lock.domain.vo.LockState;

public final class Lock extends AggregateRoot<LockId, Integer, LockSnapshot> {

  private final String name;
  private final Set<CronExpression> cronExpressions;
  private LockState state;

  private Lock(
      final LockId id,
      final String name,
      final LockState state,
      final Set<CronExpression> cronExpressions) {
    super(id);
    this.name = name;
    this.state = state;
    this.cronExpressions = new LinkedHashSet<>(cronExpressions);
  }

  public Lock(final LockSnapshot snapshot) {
    this(
        new LockId(snapshot.id()),
        snapshot.name(),
        snapshot.state(),
        snapshot.cronExpressions().stream()
            .map(CronExpression::new)
            .collect(Collectors.toCollection(LinkedHashSet::new)));
  }

  @Override
  public LockSnapshot snapshot() {
    return new LockSnapshot(
        id().value(), name, state, cronExpressions.stream().map(CronExpression::value).toList());
  }

  public void lock() {
    if (state.isLocked()) {
      return;
    }
    state = LockState.CLOSING;
    recordEvent(new LockRequested(id().value(), name));
  }

  public void addCronExpression(final CronExpression expression) {
    cronExpressions.add(expression);
  }

  public void removeCronExpression(final CronExpression expression) {
    cronExpressions.remove(expression);
  }

  public void reportStatus(final LockState state, final LockJamStatus jamStatus) {
    recordEvent(new LockStatusReported(id().value(), name, state, jamStatus));
  }

  public void reportConnected() {
    recordEvent(new LockConnected(id().value(), name));
  }

  public void reportDisconnected() {
    recordEvent(new LockDisconnected(id().value(), name));
  }

  public void reportSettingsChanged() {
    recordEvent(new LockSettingsChanged(id().value(), name));
  }

  public void reportBatteryLevel(final BatteryIndicator indicator) {
    recordEvent(
        new BatteryLevelReported(
            id().value(),
            name,
            new BatteryStatus(indicator.getLevel().value(), indicator.getCategory())));
  }
}
