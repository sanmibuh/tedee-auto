# WIP — Issue #42 / PR #98 review comment #5 (Lock aggregate)

Resolving PR #98 review comment #5: introduce a real `Lock` aggregate so the
domain drives the action (find → lock → save), instead of the handler calling
an RPC-style port. Repository-style port chosen (`findById` + `save`).

## Design (approved)

- **Domain**
  - `LockStatus` enum (domain vocabulary): `LOCKED`, `UNLOCKED`, `TRANSITIONING`, `UNKNOWN`.
  - `Lock` aggregate root: `LockId id`, `LockStatus status`, behavior `lock()`:
    - already `LOCKED` → idempotent (no command).
    - `TRANSITIONING` / `UNKNOWN` → `LockNotOperableException` (DomainException).
    - `UNLOCKED` → command transition to `LOCKED`.
  - `LockPort`: `Lock findById(LockId)` + `void save(Lock)`.
- **Application**
  - `CloseLockHandler`: `var lock = lockPort.findById(id); lock.lock(); lockPort.save(lock);`
- **Infra (`TedeeApiAdapter`)**
  - `findById` → `getLockById` → map device `state` code → `LockStatus` → build `Lock`.
  - `save` → if the lock has a commanded transition to `LOCKED`, call `postLock`; else no-op.
  - Device→domain state mapping lives here (6=closed→LOCKED, 2=open→UNLOCKED, transitional→TRANSITIONING, 9/255→UNKNOWN).

## Plan (TDD mini-steps)

1. ~~Align issue #42 naming (comment #4)~~ **done**.
2. `LockStatus` enum + `Lock.lock()` UNLOCKED→LOCKED (RED/GREEN).
3. `Lock.lock()` idempotent when already LOCKED.
4. `Lock.lock()` rejects TRANSITIONING / UNKNOWN → `LockNotOperableException`.
5. `Lock` exposes commanded-lock intent for the adapter.
6. `LockPort` → `findById` + `save`; update `CloseLockHandler` (find→close→save).
7. `TedeeApiAdapter.findById` mapping (getLockById → Lock).
8. `TedeeApiAdapter.save` → postLock only when commanded.
9. Update `ARCHITECTURE.md`; reply to PR comment #5; `./mvnw verify`.

## Open items after #5
- Comment #6 (CommandBus / inbound adapter) — likely deferred to issue #95.
- Comment #7 (`PersonalKey` vs `api_token` auth).
</content>
