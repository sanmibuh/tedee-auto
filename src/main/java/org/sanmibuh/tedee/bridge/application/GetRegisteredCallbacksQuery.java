package org.sanmibuh.tedee.bridge.application;

import java.net.URI;
import java.util.List;
import org.sanmibuh.ddd.port.Query;

public record GetRegisteredCallbacksQuery() implements Query<List<URI>> {}
