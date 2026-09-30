package org.sanmibuh.tedee.lock.infrastructure.primary;

import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.port.CommandBus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
final class TedeeEventController {

  private final CommandBus commandBus;
}
