package org.sanmibuh.tedee.lock.infrastructure.primary;

import lombok.RequiredArgsConstructor;
import org.sanmibuh.ddd.port.CommandBus;
import org.sanmibuh.tedee.lock.application.ReportLockStatusCommand;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
final class TedeeEventController {

  private static final int JAMMED = 1;

  private final CommandBus commandBus;

  @PostMapping("/tedee/events")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void receive(@RequestBody final TedeeEvent event) {
    final var data = event.data();
    commandBus.dispatch(
        new ReportLockStatusCommand(
            data.deviceId(), data.state(), data.jammed() == JAMMED, data.doorState()));
  }

  record TedeeEvent(String event, Data data) {

    record Data(int deviceId, int state, int jammed, int doorState) {}
  }
}
