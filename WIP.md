# WIP: Issue #124 — Fail fast on missing TedeeProperties

## Problem
`TedeeProperties` (`baseUrl`, `apiKey`) are not validated. Missing env vars bind as
`null`, so the app starts up and only fails at runtime, contradicting `ARCHITECTURE.md`
("fails fast at startup").

## Plan
1. RED: Test that binding blank/missing `baseUrl`/`apiKey` fails context startup.
2. GREEN: Add `spring-boot-starter-validation` dependency; annotate record with
   `@Validated` + `@NotBlank`.
3. Verify GraalVM native hints (validation reflection) are covered.
4. Run `make format` and `./mvnw verify`.

## Notes
- No validation starter currently on the classpath.
