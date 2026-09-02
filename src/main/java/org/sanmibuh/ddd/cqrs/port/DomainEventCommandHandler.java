package org.sanmibuh.ddd.cqrs.port;

import java.util.List;
import org.sanmibuh.cqrs.port.BaseCommandHandler;
import org.sanmibuh.cqrs.port.Command;
import org.sanmibuh.ddd.domain.DomainEvent;

public interface DomainEventCommandHandler<C extends Command>
    extends BaseCommandHandler<C, List<DomainEvent>> {}
