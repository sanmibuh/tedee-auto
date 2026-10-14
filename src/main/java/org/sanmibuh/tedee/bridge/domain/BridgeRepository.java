package org.sanmibuh.tedee.bridge.domain;

import org.sanmibuh.ddd.port.Repository;
import org.sanmibuh.tedee.bridge.domain.vo.BridgeId;

public interface BridgeRepository extends Repository<Bridge, BridgeId, BridgeSnapshot> {}
