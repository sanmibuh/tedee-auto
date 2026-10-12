package org.sanmibuh.tedee.lock.application;

import org.sanmibuh.ddd.port.Command;

public interface LockCommand extends Command {

  int deviceId();
}
