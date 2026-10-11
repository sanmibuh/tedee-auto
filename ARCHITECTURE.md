# Architecture

`tedee-automation` is a Spring Boot service that automates a Tedee lock through its local Bridge. It is distributed as a GraalVM native Docker image.

## Structure

The project uses hexagonal architecture. Each bounded context has `domain`, `application`, and `infrastructure` packages; infrastructure is split into `primary` adapters that initiate work and `secondary` adapters that perform external work. Shared DDD/CQRS building blocks live in `org.sanmibuh.ddd`.

- Domain code has no Spring, application, or infrastructure dependency.
- Application code has no infrastructure dependency.
- Domain events use scalar payloads only and need a `DomainEventHandler` unless explicitly annotated with `@NoSubscribersRequired`.
- Concrete aggregates, handlers, adapters, and other leaf collaborators are `final`; Spring configuration classes remain non-final for proxying.

These boundaries and event rules are enforced by ArchUnit.

## Domain Messaging

Aggregates record events; the command bus publishes them after handling; repositories persist only. `Repository.get` owns find-or-throw behavior. Event handlers, command handlers, and query handlers are discovered and registered through the DDD auto-configuration.

Failures are classified by outcome: domain violations map to 400, missing aggregates to 404, permanent integrations to 500, and transient integrations to 503.

## Lock Context

`Lock` represents an authoritative physical device. Closing is idempotent and produces a `LockRequested` intent. Bridge callbacks produce `LockStatusReported` facts; the status-report handler translates Bridge codes into domain enums before invoking the aggregate.

The scheduler issues close commands from the configured device-id-to-cron map. The webhook adapter receives Bridge callbacks, authenticates them, and dispatches status reports. The Bridge adapter executes `LockRequested` events and retries transient failures. Callback registration is lifecycle-managed and idempotent by callback URL.

The Bridge uses encrypted `api_token` authentication. A request interceptor creates a fresh token from the API key and current UTC time.

## Notifications

`Notifier` is a framework-level output port with a Telegram adapter. Credentials are required at startup, Telegram failures are mapped to integration categories, and transient failures are retried.

`LockStatusReportedNotificationHandler` renders state messages and jam alerts through `MessageSource`. Catalogs are in `messages_en.properties` and `messages_es.properties`; `spring.web.locale` selects the application locale for event-driven notifications.

## Runtime And Quality

Runtime hints cover reflection needed by Jackson and configuration binding in the native image. The generated Bridge client remains an infrastructure collaborator, not a port.

Domain and application behavior is tested through use cases with real aggregates and mocked I/O ports. Adapter tests exercise their real collaborator boundary. Error Prone, NullAway, Spotless, ArchUnit, and JaCoCo run during `./mvnw verify`.

PITest must remain at 100%; use `make pitest` for its incremental history workflow. An unavoidable equivalent mutant belongs in a small private method whose name ends in `PITEquivalent`.

## Delivery

The release workflow verifies the release state, builds and smoke-tests the native image, publishes the immutable image, then atomically publishes the release commits and tag. `latest` advances only after the release exists.

```bash
./mvnw verify
make pitest
docker build -t tedee-automation .
./mvnw -Pnative native:compile
```
