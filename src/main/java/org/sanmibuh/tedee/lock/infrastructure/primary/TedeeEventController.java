package org.sanmibuh.tedee.lock.infrastructure.primary;

import static org.sanmibuh.tedee.lock.infrastructure.TedeeInfrastructureConfiguration.*;
import static org.springframework.http.HttpStatus.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sanmibuh.ddd.domain.DomainException;
import org.sanmibuh.ddd.port.CommandBus;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@RestController
@RequiredArgsConstructor
@ImportRuntimeHints(TedeeEventRuntimeHints.class)
final class TedeeEventController {

  private final CommandBus commandBus;
  private final JsonMapper jsonMapper;

  @PostMapping(EVENTS_PATH)
  @ResponseStatus(NO_CONTENT)
  void receive(@RequestBody final TedeeEvent event) {
    TedeeEventType.from(event.event())
        .ifPresentOrElse(
            type -> commandBus.dispatch(type.toCommand(event.data(), jsonMapper)),
            () -> log.warn("Ignoring unknown Tedee Bridge event {}", event.event()));
  }

  @ExceptionHandler(DomainException.class)
  @ResponseStatus(NO_CONTENT)
  void handleDomainException(final DomainException exception) {
    log.warn("Ignoring rejected Tedee Bridge event: {}", exception.getMessage());
  }

  record TedeeEvent(String event, JsonNode data) {}
}
