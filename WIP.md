# WIP — Issue #128: EventBus and domain-event publishing pipeline

## Already done (committed)

- ~~`ddd/port`: `EventBus`, `DomainEventHandler`; `ddd/domain`: `AggregateRoot.recordEvent/domainEvents`~~
- ~~`InMemoryEventBus` — synchronous, N handlers per event type (+ tests)~~
- ~~`AggregateCommandHandler` (returns the mutated aggregate) (+ tests)~~
- ~~`DomainEventPublishingCommandBus` — drains aggregate events and publishes to `EventBus` (+ tests)~~
- ~~`CloseLockHandler` extends `AggregateCommandHandler`~~
- ~~`DomainEventHandlerRegistrar` — scans `DomainEventHandler.class` in autoconfig packages (+ tests, idempotent)~~
- ~~`AggregateRoot` identity `equals`/`hashCode` (hand-written, by concrete type + id) (+ tests)~~
- ~~Final-by-default retrofit across the project + `AGENTS.md` convention~~

## Remaining steps (wiring / registration)

- ~~RED/GREEN: `DddAutoConfiguration` exposes `EventBus` (`InMemoryEventBus`)~~
- ~~RED/GREEN: `DddAutoConfiguration` registers the `DomainEventHandlerRegistrar`~~
- [ ] RED/GREEN: `DddCqrsAutoConfiguration` exposes `DomainEventPublishingCommandBus` as the default `CommandBus` (RED done)
- [ ] RED/GREEN: priority — the `ddd.cqrs` publishing bus wins; `cqrs` `InMemoryCommandBus` stays as a fallback that backs off (`@AutoConfigureBefore` + `@ConditionalOnMissingBean`)
- [ ] Register new auto-configs (`DddAutoConfiguration`, `DddCqrsAutoConfiguration`) in `AutoConfiguration.imports`
- [ ] Update `ARCHITECTURE.md`
- [ ] `make format` + `./mvnw verify` + `make pitest`

## Design decision (confirmed with user)

The `ddd.cqrs` `DomainEventPublishingCommandBus` has priority: if no `CommandBus` bean is defined, it is created. The `cqrs` `InMemoryCommandBus` remains a fallback (`@ConditionalOnMissingBean`) that backs off because the publishing bus is already present.
