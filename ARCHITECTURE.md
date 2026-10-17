# Architecture

`tedee-automation` is a Spring Boot service that automates a Tedee lock through its local Bridge. It is distributed as a GraalVM native Docker image.

## Structure

The project uses hexagonal architecture. Each bounded context has `domain`, `application`, and `adapter` packages; adapters are split into `primary` adapters that initiate work and `secondary` adapters that perform external work. Shared DDD/CQRS building blocks live in `org.sanmibuh.ddd`.

- Domain code has no Spring, application, or adapter dependency.
- Application code has no adapter dependency.
- Domain events use scalar payloads or event-owned payload records whose fields are scalar or domain enums that represent closed vocabularies; they need a `DomainEventHandler` unless explicitly annotated with `@NoSubscribersRequired`.
- Concrete aggregates, handlers, adapters, and other leaf collaborators are `final`; Spring configuration classes remain non-final for proxying.

These boundaries and event rules are enforced by ArchUnit.

## Domain Messaging

Aggregates record events; the command bus publishes them after handling; repositories persist immutable aggregate snapshots only. When a `PlatformTransactionManager` is available, the auto-configuration makes a transactional command-bus decorator primary so handling and synchronous event publication share one transaction; otherwise it uses the in-memory bus directly. Applications can replace both defaults by declaring their own `CommandBus`. Aggregates are created through named factories and reconstructed from their snapshot types. `Repository.save(aggregate)` delegates to the concrete `save(snapshot)` adapter contract, while `Repository.get` owns find-or-throw behavior. Snapshot payloads use scalar standard types or immutable lists of them; domain-event payload rules remain stricter. Event handlers, command handlers, and query handlers are discovered and registered through the DDD auto-configuration.

Failures are classified by outcome: domain violations map to 400, missing aggregates to 404, permanent integrations to 500, and transient integrations to 503.

## Lock And Bridge Contexts

`Lock` represents an authoritative physical device. Closing is idempotent and produces a `LockRequested` intent. Bridge callbacks produce lock status, battery, connection, and settings facts; their handlers translate Bridge codes and battery percentages into domain value objects before invoking the aggregate. Battery percentages are constrained to 0-100.

`Bridge` represents the single configured physical Bridge and has a singleton identity. Its connection callbacks produce connection facts, consumed independently from the `Lock` context. The shared authenticated webhook adapter maps its closed callback vocabulary to commands through `TedeeEventType`; connection values use the documented `0` (disconnected) and `1` (connected) codes. The scheduler issues close commands from the configured device-id-to-cron map. Charging lifecycle callbacks are not supported because the managed Tedee device cannot be recharged directly. The Bridge adapter executes `LockRequested` events and retries transient failures. Callback registration is persistent: a lifecycle primary adapter dispatches `AddBridgeCallbackCommand` at startup, the Bridge repository reconstructs registered callbacks from the Bridge API, and the aggregate emits `BridgeCallbackAdded` only when the configured URL is absent. Its handler creates the callback, while shutdown never deletes it; failed reconciliation is retried by an administrator through the administration chat.

The Bridge uses encrypted `api_token` authentication. A request interceptor creates a fresh token from the API key and a process-monotonic UTC timestamp, ensuring every request has a unique token even when requests overlap within one millisecond. `CallbackQueryApi` and `CallbackCommandApi` isolate callback reads and mutations behind separate HTTP clients because the Bridge closes a persistent connection when `POST /callback` follows `GET /callback`. `LockCommandApi` isolates the generated lock client behind the supported lock operations. `TEDEE_API_KEY` and one Bridge endpoint source are required and validated at startup: set `TEDEE_BASE_URL` for a complete URL, including HTTPS and reverse-proxy paths, or set `TEDEE_HOST` for the local `http://<host>/v1.0` default.

## Notifications

`Notifier` is a framework-level output port with a Telegram adapter. Credentials are required at startup, Telegram failures are mapped to integration categories, and transient failures are retried. Operational notifications always use the required Telegram `chat-id`; the optional `admin-chat-id` identifies the administrator for future inbound administrative features and defaults to `chat-id` when omitted.

Notifications consume `Lock` and `Bridge` domain events and are rendered through `MessageSource`. Battery levels are categorized as depleted (<=10), critical (<=20), low (<=40), medium (<=60), good (<=80), or high. Catalogs are in `messages.properties` and `messages_es.properties`; `spring.web.locale` selects the application locale for event-driven notifications.

## Persistence

Persistence is infrastructure for secondary adapters: JPA entities and repositories live in adapters and domain aggregates stay persistence-agnostic. The embedded database is file-based H2 (`tedee.mv.db` on the shared data volume). It replaced SQLite because `sqlite-jdbc` loads a bundled `.so` that a static musl native binary cannot load; H2 is pure Java. The H2 console is not used.

Liquibase owns the schema and Hibernate only validates it (`ddl-auto: validate`). The application has no Liquibase dependency: migrations run from a separate, ephemeral image built from `docker/liquibase/` that applies `db/changelog` and exits before the application starts. `db/changelog/db.changelog-master.yaml` includes every file in `db/changelog/changes/`; changesets are reviewed, committed formatted-SQL files named `NNN-description.sql` (H2 is the only engine, so DDL is written exactly). JPA-based diff generation is a development aid only and never runs in CI or releases. `make migrate` applies the changelog to `./data` locally.

The H2 file format changes between major versions, so the application and migration images must use the same H2 version: `.github/scripts/build-liquibase-image.sh` resolves `h2.version` from `pom.xml` and records it as an image label.

## Runtime And Quality

Runtime hints cover reflection needed by Jackson and configuration binding in the native image. The generated Bridge client remains an adapter collaborator, not a port.

Domain and application behavior is tested through use cases with real aggregates and mocked I/O ports. Adapter tests exercise their real collaborator boundary. Error Prone, NullAway, Spotless, ArchUnit, and JaCoCo run during `./mvnw verify`.

PITest must remain at 100%; use `make pitest` for its incremental history workflow. An unavoidable equivalent mutant belongs in a small private method whose name ends in `PITEquivalent`.

## Delivery

Dockerfiles live in `docker/app` (native application image) and `docker/liquibase` (migration image). The release workflow verifies the release state, builds and smoke-tests the native image, publishes the immutable image, then atomically publishes the release commits and tag. `latest` advances only after the release exists. The migration image is published under every application release tag: when `db/`, `docker/liquibase/` and the H2 version are unchanged since the previous release it is only re-tagged to the existing digest, otherwise a new image is built and smoke-tested.

```bash
./mvnw verify
make pitest
docker build -f docker/app/Dockerfile -t tedee-automation .
make migrate
./mvnw -Pnative native:compile
```
