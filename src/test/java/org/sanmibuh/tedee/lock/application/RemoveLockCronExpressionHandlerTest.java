package org.sanmibuh.tedee.lock.application;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sanmibuh.tedee.lock.domain.Lock;
import org.sanmibuh.tedee.lock.domain.LockRepository;
import org.sanmibuh.tedee.lock.domain.LockSnapshot;
import org.sanmibuh.tedee.lock.domain.vo.LockId;
import org.sanmibuh.tedee.lock.domain.vo.LockStatus;

@ExtendWith(MockitoExtension.class)
class RemoveLockCronExpressionHandlerTest {

  private static final String WEEKDAYS_AT_22 = "0 0 22 * * MON-FRI";
  private static final String NIGHTLY = "0 30 23 * * *";

  @Mock LockRepository repository;

  @InjectMocks RemoveLockCronExpressionHandler sut;

  @Captor ArgumentCaptor<Lock> savedLock;

  private static LockSnapshot snapshotWith(final String... expressions) {
    return new LockSnapshot(1, LockStatus.UNLOCKED, List.of(expressions));
  }

  private void givenLockWithCronExpressions(final String... expressions) {
    given(repository.get(new LockId(1))).willReturn(new Lock(snapshotWith(expressions)));
  }

  @Test
  void should_removeCronExpression_whenExpressionExists() {
    givenLockWithCronExpressions(WEEKDAYS_AT_22, NIGHTLY);

    sut.handle(new RemoveLockCronExpressionCommand(1, WEEKDAYS_AT_22));

    verify(repository).save(savedLock.capture());
    final var actual = savedLock.getValue().snapshot();
    final var expected = snapshotWith(NIGHTLY);
    then(actual).isEqualTo(expected);
  }

  @Test
  void should_keepCronExpressions_whenExpressionIsAbsent() {
    givenLockWithCronExpressions(NIGHTLY);

    sut.handle(new RemoveLockCronExpressionCommand(1, WEEKDAYS_AT_22));

    verify(repository).save(savedLock.capture());
    final var actual = savedLock.getValue().snapshot();
    final var expected = snapshotWith(NIGHTLY);
    then(actual).isEqualTo(expected);
  }
}
