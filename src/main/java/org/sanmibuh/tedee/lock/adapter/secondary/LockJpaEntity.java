package org.sanmibuh.tedee.lock.adapter.secondary;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Version;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sanmibuh.tedee.lock.domain.LockSnapshot;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LockJpaEntity {

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "lock_cron_expression",
      joinColumns = @JoinColumn(name = "lock_id"),
      foreignKey = @ForeignKey(name = "fk_lock_cron_expression_lock"))
  @OrderBy
  @Column(name = "cron_expression", nullable = false, length = 512)
  private final Set<String> cronExpressions = new LinkedHashSet<>();
  @Id private int id;
  @Version private long version;

  public LockJpaEntity(final int id) {
    this.id = id;
  }

  public void update(final LockSnapshot snapshot) {
    cronExpressions.clear();
    cronExpressions.addAll(Set.copyOf(snapshot.cronExpressions()));
  }
}
