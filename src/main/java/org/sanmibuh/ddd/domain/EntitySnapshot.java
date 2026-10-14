package org.sanmibuh.ddd.domain;

public interface EntitySnapshot<T> extends Snapshot {

  T id();
}
