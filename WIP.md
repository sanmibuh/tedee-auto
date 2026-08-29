# WIP — Issue #99: Improve `org.sanmibuh.ddd.domain` base abstractions

## Goal
Model aggregate roots and their identifiers explicitly and type-safely in the shared DDD package, and codify the domain-event payload rule.

## Decisions
- DomainEvent payload rule: **documented in ARCHITECTURE.md AND enforced via ArchUnit**.
- `LockLocked` changes from `LockLocked(LockId lockId)` to `LockLocked(int deviceId)` to comply with the scalar-payload rule.

## Planned steps (TDD mini-steps)
1. Add `ValueObject<T>` interface (`T value()`).
2. Add `AggregateRootId<T>` interface extending `ValueObject<T>`.
3. Make `AggregateRoot<ID extends AggregateRootId<?>>` generic, owning/exposing `id`.
4. Update `LockId` to implement `AggregateRootId<Integer>`.
5. Update `Lock` to `extends AggregateRoot<LockId>` and pass id to super.
6. Change `LockLocked` to `LockLocked(int deviceId)`; update `Lock`, adapter, and tests.
7. Add ArchUnit rule enforcing DomainEvent scalar-only payloads.
8. Update `ARCHITECTURE.md`.
9. `make format` + `./mvnw verify`; delete WIP.md.

## Next step
Step 1 — RED for `ValueObject<T>` / `AggregateRootId<T>`.
