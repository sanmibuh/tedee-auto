# Architecture

## Overview

`tedee-automation` is a Spring Boot backend that automates a Tedee smart lock.

Built as a **GraalVM Native Image**, shipped as a Docker container via GitHub Container Registry
(`ghcr.io/sanmibuh/tedee-auto/tedee-automation`).

---

## Shared libraries

### `org.sanmibuh.ddd`
Base abstractions for DDD. Three exception categories model the possible outcomes of a failed operation, and `GlobalExceptionHandler` maps each to an HTTP status:
- `DomainException` — a business rule was violated (the request is invalid). Mapped to HTTP 400.
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
infrastructure/  — Spring beans: controllers, adapters, schedulers
```

Rules enforced at build time by ArchUnit:
- `domain` must not depend on Spring or `infrastructure`
- Cross-aggregate coupling at infrastructure level is forbidden (not yet enforced by ArchUnit — rule will be added once multiple aggregates with `infrastructure` sub-packages exist)

Failures are modelled with the three `ddd` exception categories (`DomainException` → 400, `IntegrationException` → 500, `TransientIntegrationException` → 503) and mapped globally by `GlobalExceptionHandler` (extends `ResponseEntityExceptionHandler`). Spring resolves the most-specific `@ExceptionHandler`, so a transient failure yields 503 rather than 500.

---

## Bounded context: `tedee.lock`

Automates operations against a physical Tedee lock exposed through a **Tedee Bridge** on the local network.

**`domain`** — pure business logic:
- `LockId` — value object wrapping the device id, with a guard clause (`deviceId > 0`).
- `LockPort` — secondary (output) port the domain depends on; decoupled from any HTTP or generated-client type.
- Exceptions, each modelling an outcome rather than a specific bridge cause. Every bridge-translated exception wraps the original `RestClientException` as its cause, so no infrastructure exception ever leaks into the domain:
  - `InvalidLockIdException` (extends `DomainException`, → 400) — invalid `LockId` construction.
  - `InvalidLockRequestException` (extends `DomainException`, → 400) — the bridge rejected the request as invalid (bridge HTTP 404, device unknown).
  - `LockOperationFailedException` (extends `IntegrationException`, → 500) — the bridge failed to perform the operation for a non-recoverable reason (bridge HTTP 401, any 5xx, and any other unmapped status).
  - `LockTemporarilyUnavailableException` (extends `TransientIntegrationException`, → 503) — the lock is momentarily unreachable and the operation may succeed if retried (bridge HTTP 405 disconnected, 406 Bluetooth error, or a connectivity failure such as connection refused / timeout).

**`application`** — commands and handlers:
- `CloseLockCommand` — carries the primitive `int deviceId`.
- `CloseLockHandler` — builds the `LockId` and delegates to `LockPort`.

**`infrastructure`** — Tedee Bridge secondary adapter:
- `TedeeApiAdapter` — implements `LockPort`. Delegates to the generated `LockApi` (`POST /v1.0/lock/{deviceId}/lock`, expects `204`) and translates every `RestClientException` (HTTP error responses via status, and connectivity failures) into the `ddd` exception categories, so no `RestClient` exception escapes the port. Unexpected non-`RestClientException` errors are left to propagate to the global handler as `500`.
- `TedeeClientConfiguration` — wires the generated `ApiClient`/`LockApi` from an injected `RestClient.Builder`, setting the base URL and the `api_token` API key.
- `TedeeProperties` — `@ConfigurationProperties(prefix = "sanmibuh.rest.tedee")` holding `baseUrl` and `apiKey`. Both are sourced from the mandatory `TEDEE_HOST` and `TEDEE_API_KEY` environment variables (`base-url: http://${TEDEE_HOST}/v1.0`, `api-key: ${TEDEE_API_KEY}`); neither has a default, so the application **fails fast at startup** if either is unset rather than issuing requests against an invalid URL or without credentials.

### Generated Tedee client
The Bridge client is generated offline by `openapi-generator-maven-plugin` (`7.25.0`, flavour `java` + `library=restclient`) from the vendored spec `openapi/tedee-bridge-api.json`. Generated code lives in package `com.tedee.bridge.client.*` (outside `org.sanmibuh`, so NullAway/Error Prone treat it as third-party) and is emitted to `target/generated-sources/openapi` (not under `src/`, so Spotless ignores it). The generated client is a **collaborator** of `TedeeApiAdapter`, never the port itself.

> **GraalVM note:** the current `lock` flow never (de)serializes the generated Jackson models — success is `204` with no body and errors are handled by status code only. Reflection hints for `com.tedee.bridge.client.model.*` are therefore **deferred** (tracked in #96) and must be added (via a `RuntimeHintsRegistrar` or `reflect-config.json`) as soon as response bodies start being parsed.

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
