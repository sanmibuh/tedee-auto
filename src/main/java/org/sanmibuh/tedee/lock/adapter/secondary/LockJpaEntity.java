package org.sanmibuh.tedee.lock.adapter.secondary;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderBy;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LockJpaEntity {

  @Id private int id;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "lock_cron_expression", joinColumns = @JoinColumn(name = "lock_id"))
  @OrderBy
  @Column(name = "cron_expression", nullable = false, length = 512)
  private Set<String> cronExpressions = new LinkedHashSet<>();

  public LockJpaEntity(final int id, final Set<String> cronExpressions) {
    this.id = id;
    this.cronExpressions = new LinkedHashSet<>(cronExpressions);
  }
}
