# Architecture

## Overview

`tedee-automation` is a Spring Boot backend that automates a Tedee smart lock.

Built as a **GraalVM Native Image**, shipped as a Docker container via GitHub Container Registry
(`ghcr.io/sanmibuh/tedee-auto/tedee-automation`).

---

## Shared libraries

### `org.sanmibuh.ddd`
Base abstractions for DDD:
- `DomainException` — base `RuntimeException` for all domain rule violations. Subclasses are mapped automatically to HTTP 400 by `GlobalExceptionHandler`.

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
infrastructure/  — Spring beans: controllers, adapters, schedulers
```

Rules enforced at build time by ArchUnit:
- `domain` must not depend on Spring or `infrastructure`
- Cross-aggregate coupling at infrastructure level is forbidden (not yet enforced by ArchUnit — rule will be added once multiple aggregates with `infrastructure` sub-packages exist)

Domain rule violations extend `DomainException` and are mapped globally to HTTP 400 by `GlobalExceptionHandler` (extends `ResponseEntityExceptionHandler`).

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
