# Copilot instructions

Project-specific context for GitHub Copilot (including code review). For the full contributor guide see `AGENTS.md` and `ARCHITECTURE.md`.

## Non-null by default (NullAway)

The `org.sanmibuh` packages are non-null by default, enforced at compile time by NullAway as an error (`-Xep:NullAway:ERROR`, `AnnotatedPackages=org.sanmibuh`). Treat every field, parameter, and return type as non-null unless it is explicitly annotated `@Nullable`.

Consequences for review:
- Do not flag potential `NullPointerException` for passing `null` to a non-`@Nullable` parameter or constructor: that is a compile error, not a runtime risk.
- Do not request defensive null checks (e.g. `Objects.requireNonNull`, `if (x == null)`) on non-`@Nullable` values. They are redundant.
- Domain value objects (e.g. `LockId`) are constructed in handlers from primitive command fields, not deserialized from external JSON, so their guard clauses only need to cover domain-invalid values, not `null`.

## Architecture

Hexagonal (Ports & Adapters) with DDD and CQRS. The `domain` layer must not depend on Spring or `infrastructure` (enforced by ArchUnit). Commands and queries carry only primitive/standard Java types; handlers construct domain objects from them. Domain events carry only scalar snapshot payloads (enforced by ArchUnit).

## Testing conventions

- Behavior-focused, collaborative (sociable) unit tests at the use-case boundary: the handler is the `sut`, exercised with a real aggregate, mocking only the port.
- A rule tested once at its source is not re-tested at every caller. For example, the not-found policy lives in `Repository.get(...)` and is covered in `RepositoryTest`; individual handlers are not expected to duplicate that scenario.
- BDD assertions via AssertJ `BDDAssertions` (`then(...)`, `thenThrownBy(...)`); the subject under test is always named `sut`.

## Formatting

Spotless (google-java-format, 2-space indent) is the single source of truth and fails the build. Do not raise formatting/style comments; defer to Spotless.
