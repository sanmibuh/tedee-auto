package org.sanmibuh.tedee.automation.configuration;

import lombok.extern.slf4j.Slf4j;
import org.sanmibuh.ddd.domain.AggregateNotFoundException;
import org.sanmibuh.ddd.domain.DomainException;
import org.sanmibuh.ddd.domain.IntegrationException;
import org.sanmibuh.ddd.domain.TransientIntegrationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(DomainException.class)
  public ProblemDetail handleDomainException(final DomainException ex) {
    final var detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    detail.setDetail(ex.getMessage());

    return detail;
  }

  @ExceptionHandler(AggregateNotFoundException.class)
  public ProblemDetail handleAggregateNotFoundException(final AggregateNotFoundException ex) {
    final var detail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
    detail.setDetail(ex.getMessage());

    return detail;
  }

  @ExceptionHandler(TransientIntegrationException.class)
  public ProblemDetail handleTransientIntegrationException(final TransientIntegrationException ex) {
    log.warn("Transient integration failure", ex);
    return ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);
  }

  @ExceptionHandler(IntegrationException.class)
  public ProblemDetail handleIntegrationException(final IntegrationException ex) {
    log.error("Integration failure", ex);
    return ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleUnexpectedException(final Exception ex) {
    log.error("Unexpected error", ex);
    return ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
