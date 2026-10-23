package org.sanmibuh.tedee.lock.adapter.secondary.persistence;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.BDDMockito.given;

import com.mickaelb.api.AssertHibernateSQLCount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import java.util.List;
import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sanmibuh.ddd.domain.AggregateNotFoundException;
import org.sanmibuh.framework.persistence.RepositoryTest;
import org.sanmibuh.tedee.lock.adapter.secondary.rest.LockGateway;
import org.sanmibuh.tedee.lock.adapter.secondary.rest.LockView;
import org.sanmibuh.tedee.lock.domain.LockSnapshot;
import org.sanmibuh.tedee.lock.domain.vo.LockId;
import org.sanmibuh.tedee.lock.domain.vo.LockState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

@ExtendWith(SoftAssertionsExtension.class)
@RepositoryTest
@Import(TedeeLockRepository.class)
class TedeeLockRepositoryTest {

  private static final int DEVICE_ID = 42;
  private static final String NAME = "Front door";
  private static final String WEEKDAYS_AT_22 = "0 0 22 * * MON-FRI";
  private static final String NIGHTLY = "0 30 23 * * *";

  @InjectSoftAssertions BDDSoftAssertions softly;

  @MockitoBean LockGateway lockGateway;

  @Autowired TedeeLockRepository sut;
  @Autowired EntityManager entityManager;
  @Autowired JdbcTemplate jdbcTemplate;

  @BeforeEach
  void stubGateway() {
    stubReportedState(LockState.OPEN);
  }

  @Test
  @AssertHibernateSQLCount(selects = 1)
  void should_returnUnlockedLockWithoutCronExpressions_whenLockWasNeverSaved() {
    then(sut.get(new LockId(DEVICE_ID)).snapshot())
        .isEqualTo(new LockSnapshot(DEVICE_ID, NAME, LockState.OPEN, List.of()));
  }

  @Test
  @Sql("/sql/lock-with-cron-expressions.sql")
  @AssertHibernateSQLCount(selects = 1)
  void should_returnPersistedCronExpressions_whenFindingStoredLock() {
    final var expected =
        new LockSnapshot(DEVICE_ID, NAME, LockState.OPEN, List.of(WEEKDAYS_AT_22, NIGHTLY));

    final var actual = sut.get(new LockId(DEVICE_ID)).snapshot();

    then(actual).isEqualTo(expected);
  }

  @Test
  @Sql("/sql/lock-with-cron-expressions.sql")
  @AssertHibernateSQLCount(selects = 1, deletes = 1, updates = 1)
  void should_replaceCronExpressionsAndIncrementVersion_whenSavingAgain() {
    saveAndFlush(new LockSnapshot(DEVICE_ID, NAME, LockState.OPEN, List.of(NIGHTLY)));

    softly.then(storedCronExpressions()).containsExactly(NIGHTLY);
    softly.then(storedVersions()).containsExactly(1L);
  }

  @Test
  @Sql("/sql/lock-with-cron-expressions.sql")
  @AssertHibernateSQLCount(selects = 1, updates = 1)
  void should_incrementVersion_whenSavingUnchangedLock() {
    saveAndFlush(sut.get(new LockId(DEVICE_ID)).snapshot());

    then(storedVersions()).containsExactly(1L);
  }

  @Test
  @Sql("/sql/lock-with-cron-expressions.sql")
  void should_failOnFlush_whenLockWasModifiedConcurrently() {
    sut.get(new LockId(DEVICE_ID));
    jdbcTemplate.update("UPDATE lock SET version = version + 1 WHERE id = ?", DEVICE_ID);

    sut.save(new LockSnapshot(DEVICE_ID, NAME, LockState.OPEN, List.of(NIGHTLY)));

    thenThrownBy(entityManager::flush).isInstanceOf(OptimisticLockException.class);
  }

  @Test
  @AssertHibernateSQLCount(selects = 2, inserts = 1)
  void should_notPersistLockStatus_whenSavingLockedLock() {
    sut.save(new LockSnapshot(DEVICE_ID, NAME, LockState.CLOSED, List.of()));
    stubReportedState(LockState.OPEN);

    then(sut.get(new LockId(DEVICE_ID)).snapshot().state()).isEqualTo(LockState.OPEN);
  }

  @Test
  @AssertHibernateSQLCount(selects = 1)
  void should_returnReportedStatus_whenBridgeReportsLockedLock() {
    stubReportedState(LockState.CLOSED);

    then(sut.get(new LockId(DEVICE_ID)).snapshot().state()).isEqualTo(LockState.CLOSED);
  }

  @Test
  @AssertHibernateSQLCount(selects = 1)
  void should_throwNotFound_whenBridgeDoesNotKnowTheLock() {
    final var lockId = new LockId(DEVICE_ID);
    given(lockGateway.get(lockId)).willThrow(new AggregateNotFoundException(lockId));

    thenThrownBy(() -> sut.get(lockId)).isInstanceOf(AggregateNotFoundException.class);
  }

  private void stubReportedState(final LockState state) {
    given(lockGateway.get(new LockId(DEVICE_ID))).willReturn(new LockView(DEVICE_ID, NAME, state));
  }

  private void saveAndFlush(final LockSnapshot snapshot) {
    sut.save(snapshot);
    entityManager.flush();
  }

  private List<Long> storedVersions() {
    return jdbcTemplate.queryForList(
        "SELECT version FROM lock WHERE id = ?", Long.class, DEVICE_ID);
  }

  private List<String> storedCronExpressions() {
    return jdbcTemplate.queryForList(
        "SELECT cron_expression FROM lock_cron_expression WHERE lock_id = ?",
        String.class,
        DEVICE_ID);
  }
}
