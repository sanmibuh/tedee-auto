package org.sanmibuh.ddd.adapter;

import org.sanmibuh.ddd.port.Command;
import org.sanmibuh.ddd.port.CommandBus;
import org.springframework.transaction.annotation.Transactional;

public interface TransactionalCommandBus extends CommandBus {

  @Override
  @Transactional
  void dispatch(Command command);
}
