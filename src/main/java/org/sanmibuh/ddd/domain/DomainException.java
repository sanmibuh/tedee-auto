package org.sanmibuh.ddd.domain;

import java.io.Serial;

public abstract class DomainException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = -8371228740000530350L;

  protected DomainException(final String message) {
    super(message);
  }
}
