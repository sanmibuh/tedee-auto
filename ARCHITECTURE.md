# Architecture

## Overview

`tedee-automation` is a Spring Boot backend that automates a Tedee smart lock.

Built as a **GraalVM Native Image**, shipped as a Docker container via GitHub Container Registry (`ghcr.io/sanmibuh/tedee-auto/tedee-automation`).

---

## Shared libraries

### `org.sanmibuh.ddd`
Base abstractions for DDD.

**Aggregate modelling:**
- `ValueObject<T>` — shared interface for value objects. Exposes the underlying primitive/standard value through `T value()`, standardising how primitive-wrapping domain types surface their raw value.
- `AggregateRootId<T>` — interface for aggregate identifiers, extends `ValueObject<T>`. Makes aggregate ids first-class DDD concepts and gives a shared type to constrain aggregates.
- `AggregateRoot<ID extends AggregateRootId<?>>` — base class for aggregate roots, generic over its identifier type. Owns the aggregate `id` (exposed via `id()`), records domain events via the protected `recordEvent(DomainEvent)`, and exposes them through the read-only `domainEvents()` (an unmodifiable list). Secondary adapters read these events to decide what to persist.
- `Repository<A extends AggregateRoot<ID>, ID extends AggregateRootId<?>>` — generic secondary (output) port for aggregate persistence, following the DDD Repository pattern. Declares `Optional<A> findById(ID)` and `save(A)` (both implemented by adapters), plus a default `get(ID)` that centralises the not-found policy (`findById(id).orElseThrow(() -> new AggregateNotFoundException(id))`). `findById` keeps the present/absent reporting in infrastructure; `get` gives handlers a `find-or-throw` primitive so they never repeat that policy. Aggregate-specific ports (e.g. `LockRepository`) extend it.
- `DomainEvent` — marker interface for domain events (business facts recorded by an aggregate, e.g. `LockLocked`). **Payload rule:** domain events must carry only primitive or standard scalar values (e.g. `String`, `UUID`, `int`, `Instant`, enums) — never value objects, aggregates, or other rich domain types. This keeps events stable, serializable, and integration-friendly, avoids coupling subscribers to internal domain structures, and aligns with the rule that commands and queries use only primitive or standard Java types. The rule is both documented here and **enforced by ArchUnit** (`HexagonalArchitectureTest.should_carryOnlyScalarPayloads_whenDomainEvent`), which inspects the fields of every `DomainEvent` implementation and rejects any non-scalar payload.

**Exceptions** — categories model the possible outcomes of a failed operation, and `GlobalExceptionHandler` maps each to an HTTP status:
- `DomainException` — a business rule was violated (the request is invalid). Mapped to HTTP 400.
- `AggregateNotFoundException` (extends `DomainException`) — a requested aggregate does not exist. Mapped to HTTP 404 (a more-specific handler than the generic `DomainException` → 400).
- `IntegrationException` — an external dependency failed in a way that is not the caller's fault. Mapped to HTTP 500.
- `TransientIntegrationException` (extends `IntegrationException`) — an external dependency is temporarily unavailable and the operation may succeed if retried. Mapped to HTTP 503.

### `org.sanmibuh.cqrs`
CQRS building blocks.

**`domain`** — framework-agnostic interfaces:
- `Command`, `CommandHandler<C>`, `CommandBus`
- `Query<R>`, `QueryHandler<Q, R>`, `QueryBus`
- `HandlerNotFoundException` — thrown when no handler is registered for a given command or query type

**`infrastructure`** — Spring-backed implementations:
- `SimpleCommandBus` / `SimpleQueryBus` — resolve handlers at startup via `HandlerLookup` (O(1) dispatch)
- `HandlerLookup` — builds a `Map<messageType, handler>` on construction using `GenericTypeResolver`
- `CQRSAutoConfiguration` — `@AutoConfiguration` that registers `SimpleCommandBus` and `SimpleQueryBus` as `@ConditionalOnMissingBean` beans (allowing applications to override either with a custom implementation), and scans `org.sanmibuh` for all `CommandHandler` / `QueryHandler` implementations. Registered in `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`.

---

## Architectural style: Hexagonal (Ports & Adapters)

Each bounded context is organised into three layers:

```
domain/          — pure business logic, no framework dependencies
application/     — commands, queries and their handlers
infrastructure/  — Spring beans, split into driving and driven adapters:
  primary/       — driving adapters (incoming channels: schedulers, controllers, …)
  secondary/     — driven adapters (outgoing channels: repositories, external clients, …)
```

Rules enforced at build time by ArchUnit (`HexagonalArchitectureTest`):
- `domain` must not depend on Spring, `infrastructure`, or `application`
- `application` must not depend on `infrastructure`
- `primary` slice must not depend on the business `domain` layer (`org.sanmibuh.tedee..domain..`) or on `secondary` — it may only consume `application` types (commands, queries, projections) plus CQRS/DDD framework abstractions
- `secondary` slice must not depend on `primary` or `application` — it may only depend on `domain` (ports, aggregates, value objects)
- Domain events (`DomainEvent` implementations) must carry only primitive or standard scalar payloads
- Cross-aggregate coupling at infrastructure level is forbidden (not yet enforced by ArchUnit — rule will be added once multiple aggregates with `infrastructure` sub-packages exist)

Failures are modelled with the `ddd` exception categories (`DomainException` → 400, `AggregateNotFoundException` → 404, `IntegrationException` → 500, `TransientIntegrationException` → 503) and mapped globally by `GlobalExceptionHandler` (extends `ResponseEntityExceptionHandler`). Spring resolves the most-specific `@ExceptionHandler`, so a transient failure yields 503 rather than 500, and a missing aggregate yields 404 rather than 400.

---

## Bounded context: `tedee.lock`

Automates operations against a physical Tedee lock exposed through a **Tedee Bridge** on the local network.

### Aggregate: `Lock`

The `Lock` aggregate (identified by a `LockId` wrapping the positive device id) owns a single piece of state: whether it is confirmed `LOCKED` or `UNLOCKED` (where `UNLOCKED` means "not confirmed locked"). Its only behaviour, `lock()`, is **idempotent**: it transitions to `LOCKED` and records a `LockLocked` event only when not already locked. The lock is treated as **authoritative** — the aggregate never rejects the operation on a safety invariant; a bridge that refuses surfaces as an infrastructure failure, not a domain rule violation. The `LockLocked` event carries only the scalar device id, per the shared `DomainEvent` payload rule.

Because the behaviour is this simple, the aggregate has no direct test of its own; its invariants are asserted through the use cases below (see the testing strategy).

### Use cases

| Use case | Command / Query | Handler behaviour |
|----------|-----------------|-------------------|
| Close a lock | `CloseLockCommand(deviceId)` | `find → mutate → save`: loads the `Lock` (missing → `AggregateNotFoundException` → 404), calls `lock()`, persists it. |

### Ports & adapters

The single **driven (output) port** is `LockRepository` (`extends Repository<Lock, LockId>`), decoupled from any HTTP or generated-client type; absence is reported as an empty `Optional`, and the find-or-throw policy lives in the shared `Repository.get` default rather than in any adapter.

- **Primary (driving) adapter — cron scheduler.** The automation is triggered by *time*: a configurable `deviceId → cron` map (`sanmibuh.scheduler.lock.schedules`) is turned into one Spring `CronTask` per lock, each publishing `CloseLockCommand(deviceId)` onto the `CommandBus`. Registration is **programmatic** (a `SchedulingConfigurer`, not a fixed `@Scheduled`) precisely so every lock can carry its own independent cron expression. Schedules are supplied per environment (e.g. `SANMIBUH_SCHEDULER_LOCK_SCHEDULES_12345="0 0 0,22 * * *"`); no device id is hard-coded, and an absent map binds to empty (no tasks) rather than `null`.
- **Secondary (driven) adapter — Tedee Bridge.** Implements `LockRepository` by reacting to the aggregate's recorded events: each `LockLocked` becomes a `POST /lock/{deviceId}/lock` on the generated bridge client. Every `RestClientException` is translated into a `ddd` exception **category** — so no client exception escapes the port — following the outcome-based mapping below. `findById` is currently a **placeholder** returning an `UNLOCKED` `Lock`; its real implementation (reading the device `state` and mapping bridge 404 → empty `Optional`) is deferred. The bridge base URL and API key come from the mandatory `TEDEE_HOST` / `TEDEE_API_KEY` environment variables (no defaults), so the application **fails fast at startup** if either is unset.

### Failure model

Bridge failures are mapped to `ddd` exception categories by outcome (not by specific cause), each wrapping the original `RestClientException` so nothing infrastructure-specific leaks into the domain:

| Category (→ HTTP) | Meaning | Bridge triggers |
|-------------------|---------|-----------------|
| `InvalidLockIdException` (→ 400) | invalid `LockId` construction | — (domain guard) |
| `InvalidLockRequestException` (→ 400) | the bridge rejected the request as invalid | HTTP 404 (device unknown) |
| `LockOperationFailedException` (→ 500) | non-recoverable bridge failure | HTTP 401, 500, any unmapped status |
| `LockTemporarilyUnavailableException` (→ 503) | momentarily unreachable, retry may succeed | HTTP 405/406, gateway 502/503/504, connectivity failures |

### Generated Tedee client
The Bridge client is generated offline by `openapi-generator-maven-plugin` (`7.25.0`, flavour `java` + `library=restclient`) from the vendored spec `openapi/tedee-bridge-api.json`. Generated code lives in package `com.tedee.bridge.client.*` (outside `org.sanmibuh`, so NullAway/Error Prone treat it as third-party) and is emitted to `target/generated-sources/openapi` (not under `src/`, so Spotless ignores it). The generated client is a **collaborator** of the secondary adapter, never the port itself.

> **GraalVM note:** the current `lock` flow never (de)serializes the generated Jackson models — success is `204` with no body and errors are handled by status code only. Reflection hints for `com.tedee.bridge.client.model.*` are therefore **deferred** (tracked in #96) and must be added (via a `RuntimeHintsRegistrar` or `reflect-config.json`) as soon as response bodies start being parsed.

---


## Testing strategy

Domain and application logic is verified with **collaborative (sociable) unit tests** written at the use-case boundary — the command/query handler:

- The **handler is the subject under test** (`sut`), exercised with a **real aggregate**. Business logic (aggregate state transitions, event recording, idempotency) is never mocked; it is asserted *through* the use case. `CloseLockHandlerTest` is the reference example.
- Only the **port** (the I/O boundary, e.g. `LockRepository`) is mocked, since it represents an actuator/side effect. The handler's output is asserted on what it hands to `save(...)` — an `ArgumentCaptor<Lock>` inspecting the aggregate's immutable `domainEvents()` rather than mutable getters (the aggregate exposes no state getters).
- **Aggregate invariants get their own direct tests only when they grow complex.** Simple behaviour stays expressed as use-case scenarios; there is no separate `LockTest` while `lock()` is a trivial idempotent transition.
- **Value-object guard clauses** are covered by focused micro-tests (e.g. `LockIdTest`), asserting only invalid input — successful construction is covered indirectly when the value object is used.
- **Secondary adapters** are tested against the real collaborator boundary (`TedeeLockRepositoryTest` uses `@RestClientTest` + `MockRestServiceServer` to assert HTTP interactions and exception translation).
- **Primary adapters** are tested as fast sociable unit tests at their own boundary rather than with a full Spring context: `LockSchedulingConfigurerTest` drives `configureTasks` with a real `ScheduledTaskRegistrar`, asserting both that a `CronTask` is registered with the configured expression and that running its runnable calls `LockScheduler.closeLock(deviceId)` (the `CommandBus` is mocked). A `@SpringBootTest` scheduling test is deliberately avoided — it would be slow, flaky, and would verify Spring wiring rather than our logic.

Mutation testing (**PITest**, `targetClasses = org.sanmibuh.*`) guards the strength of these tests and is expected to stay at 100%. It is not bound to `verify`; run it explicitly with `make pitest`, the canonical incremental entrypoint (use the raw `./mvnw test-compile org.pitest:pitest-maven:mutationCoverage` goal only for a deliberate non-incremental run without history).

PITest runs **incrementally** both locally and in CI, sharing a single history contract. History is persisted on the orphan `coverage-data` branch as `pitest-history.bin`; a run consumes it as `.pit/history-input.bin` and produces `.pit/history-output.bin` (paths configured on the `pitest-maven` plugin in `pom.xml`). `make pitest` is the canonical local entrypoint: it fetches `coverage-data`, restores the history, runs the analysis, and prints explicitly whether the run is incremental or a full baseline. When `coverage-data` is missing or contains no usable history, it falls back to a baseline run rather than failing silently. On merge to `main`, the `Main — Save Coverage` workflow copies the new `.pit/history-output.bin` back to `coverage-data:pitest-history.bin`, closing the loop so the next local or PR run starts incrementally.

---

## Static analysis

The build runs **Error Prone** (via the `javac` plugin) and **NullAway** on every compilation:

- **Error Prone** (`com.google.errorprone:error_prone_core`) — catches common Java bugs at compile time.
- **NullAway** (`com.uber.nullaway:nullaway`) — enforces non-null contracts across all packages under `org.sanmibuh`. Any `@NonNull` field that is not initialized in the constructor (e.g., Spring lifecycle fields) must be annotated with `@SuppressWarnings("NullAway.Init")`.

The required JVM flags for these tools (`--add-exports`/`--add-opens` on `jdk.compiler`) are declared in `.mvn/jvm.config` and apply automatically to all Maven goals.

---

## Build & run

```bash
# Run tests
./mvnw verify

# Build native binary (inside Docker — used by CI)
docker build -t tedee-automation .

# Build native binary locally (requires GraalVM 25)
./mvnw -Pnative native:compile
```
