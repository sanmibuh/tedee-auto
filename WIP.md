# WIP — Issue #128: EventBus and domain-event publishing pipeline

## Planned steps

- [ ] RED: `EventHandler` + `EventBus` interfaces (stubs) + `InMemoryEventBus` dispatches to a single handler
- [ ] GREEN: Implement `InMemoryEventBus` with single-handler dispatch
- [ ] RED: `InMemoryEventBus` dispatches to N handlers per event type
- [ ] GREEN: Extend to multi-handler lookup
- [ ] RED: `CommandHandler` returns `List<DomainEvent>`; `AggregateCommandHandler` base
- [ ] GREEN: Implement `AggregateCommandHandler`; update `CloseLockHandler`
- [ ] RED: `InMemoryCommandBus.dispatch` drains and publishes events
- [ ] GREEN: Wire `EventBus` into `InMemoryCommandBus`
- [ ] Update `CQRSHandlerRegistrar` to scan `EventHandler.class`
- [ ] Update existing tests broken by `CommandHandler` contract change
- [ ] `make format` + `./mvnw verify` + `make pitest`

## Open decisions
- `MultiHandlerLookup` as a new class vs extending `HandlerLookup` — will add a separate `MultiHandlerLookup` to keep `HandlerLookup` (single-handler, duplicate-guard) for commands/queries unchanged.
