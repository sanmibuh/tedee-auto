package com.example;

import org.sanmibuh.cqrs.port.Query;
import org.sanmibuh.cqrs.port.QueryHandler;

public class StubQueryHandler implements QueryHandler<StubQueryHandler.StubQuery, String> {

  public record StubQuery() implements Query<String> {}

  @Override
  public String handle(final StubQuery query) {
    return "stub-result";
  }
}
