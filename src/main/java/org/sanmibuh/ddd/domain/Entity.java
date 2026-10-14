package org.sanmibuh.ddd.domain;

public abstract class Entity<I extends EntityId<T>, T, S extends EntitySnapshot<T>> {

  private final I id;

  protected Entity(final I id) {
    this.id = id;
  }

  public final I id() {
    return id;
  }

  public abstract S snapshot();

  @Override
  @SuppressWarnings("EqualsGetClass")
  public final boolean equals(final Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    return id.equals(((Entity<?, ?, ?>) o).id);
  }

  @Override
  public final int hashCode() {
    return id.hashCode();
  }
}
