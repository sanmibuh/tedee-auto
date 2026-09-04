# Copilot instructions

The single source of truth for this project is **[`AGENTS.md`](../AGENTS.md)** (contributor guide, quality standards, TDD, non-null-by-default, testing conventions) and **[`ARCHITECTURE.md`](../ARCHITECTURE.md)** (hexagonal/DDD/CQRS structure). Read both before generating code or review comments. Everything below is not repeated here on purpose — follow those files.

The notes below only translate those rules into review-specific consequences that are easy to get wrong.

## Review consequences of non-null by default

`org.sanmibuh` is non-null by default, enforced at compile time by NullAway as an error (see the "Non-null by default" section in `AGENTS.md`). Therefore, when reviewing:

- Do not flag potential `NullPointerException` for passing `null` to a non-`@Nullable` parameter or constructor: that is a compile error, not a runtime risk.
- Do not request defensive null checks (e.g. `Objects.requireNonNull`, `if (x == null)`) on non-`@Nullable` values. They are redundant.
- Domain value objects (e.g. `LockId`) are constructed in handlers from primitive command fields, not deserialized from external JSON, so their guard clauses only need to cover domain-invalid values, not `null`.

## Review consequences for testing

- A rule tested once at its source is not re-tested at every caller. For example, the not-found policy lives in `Repository.get(...)` and is covered in `RepositoryTest`; individual handlers are not expected to duplicate that scenario.

## Formatting

Spotless (google-java-format, 2-space indent) is the single source of truth and fails the build. Do not raise formatting/style comments; defer to Spotless.
