package org.sanmibuh.tedee.lock.domain;

import java.io.Serial;
import org.sanmibuh.ddd.domain.DomainException;

public class InvalidApiTokenException extends DomainException {

  @Serial private static final long serialVersionUID = 1L;

  public InvalidApiTokenException() {
    super("Invalid Tedee API token");
  }
}
