package org.sanmibuh.tedee.lock.adapter.secondary;

import static org.assertj.core.api.BDDAssertions.then;

import com.mickaelb.api.AssertHibernateSQLCount;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.sanmibuh.framework.persistence.RepositoryTest;
import org.sanmibuh.tedee.lock.domain.LockSnapshot;
import org.sanmibuh.tedee.lock.domain.vo.LockId;
import org.sanmibuh.tedee.lock.domain.vo.LockStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

@RepositoryTest
@Import(TedeeLockRepository.class)
class TedeeLockRepositoryTest {

  private static final int DEVICE_ID = 42;
  private static final String WEEKDAYS_AT_22 = "0 0 22 * * MON-FRI";
  private static final String NIGHTLY = "0 30 23 * * *";

  @Autowired TedeeLockRepository sut;
  @Autowired EntityManager entityManager;
  @Autowired JdbcTemplate jdbcTemplate;

  @Test
  @AssertHibernateSQLCount(selects = 1)
  void should_returnUnlockedLockWithoutCronExpressions_whenLockWasNeverSaved() {
    then(sut.get(new LockId(DEVICE_ID)).snapshot())
        .isEqualTo(new LockSnapshot(DEVICE_ID, LockStatus.UNLOCKED, List.of()));
  }

  @Test
  @Sql("/sql/lock-with-cron-expressions.sql")
  @AssertHibernateSQLCount(selects = 1)
  void should_returnPersistedCronExpressions_whenFindingStoredLock() {
    final var expected =
        new LockSnapshot(DEVICE_ID, LockStatus.UNLOCKED, List.of(WEEKDAYS_AT_22, NIGHTLY));

    final var actual = sut.get(new LockId(DEVICE_ID)).snapshot();

    then(actual).isEqualTo(expected);
  }

  @Test
  @Sql("/sql/lock-with-cron-expressions.sql")
  @AssertHibernateSQLCount(selects = 1, deletes = 1)
  void should_replacePersistedCronExpressions_whenSavingAgain() {
    sut.save(new LockSnapshot(DEVICE_ID, LockStatus.UNLOCKED, List.of(NIGHTLY)));
    entityManager.flush();

    then(storedCronExpressions()).containsExactly(NIGHTLY);
  }

  @Test
  void should_notPersistLockStatus_whenSavingLockedLock() {
    sut.save(new LockSnapshot(DEVICE_ID, LockStatus.LOCKED, List.of()));

    then(sut.get(new LockId(DEVICE_ID)).snapshot().status()).isEqualTo(LockStatus.UNLOCKED);
  }

  private List<String> storedCronExpressions() {
    return jdbcTemplate.queryForList(
        "SELECT cron_expression FROM lock_cron_expression WHERE lock_id = ?",
        String.class,
        DEVICE_ID);
  }
}
