package org.sanmibuh.ddd.port;

import org.springframework.transaction.annotation.Transactional;

public interface TransactionalCommandBus extends CommandBus {

  @Override
  @Transactional
  void dispatch(Command command);
}
