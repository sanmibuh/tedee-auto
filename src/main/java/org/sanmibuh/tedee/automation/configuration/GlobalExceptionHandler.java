package org.sanmibuh.tedee.automation.configuration;

import org.sanmibuh.ddd.domain.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(DomainException.class)
  public ProblemDetail handleDomainException(final DomainException ex) {
    final var detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    detail.setDetail(ex.getMessage());

    return detail;
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleUnexpectedException(final Exception ex) {
    log.error("Unexpected error", ex);
    return ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
