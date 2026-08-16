# Agent Guidelines

Instructions for AI assistants working on this project.

---

## Project Overview

`tedee-automation` is a Spring Boot backend for automating a Tedee smart lock, built as a GraalVM Native Image and shipped as a Docker container via GitHub Container Registry.

**Tech stack:** Java 25, Spring Boot, Lombok, AssertJ (BDD), ArchUnit, PITest, Spotless (Google Java format), GraalVM.

**Architecture:** Hexagonal (Ports & Adapters) with DDD and CQRS. See `ARCHITECTURE.md` for full details.

---

## Quality Standards

These standards apply to all code in this project and are the basis for any code review.

### TDD
- Every piece of implementation code must have a corresponding test.
- Tests must test behavior, not implementation details.
- Test method names: `should_doSomething_whenCondition` (snake_case with camelCase segments).
- Use `BDDAssertions` from AssertJ: `then(...)` and `thenThrownBy(...)`. Never `assertThat` or `assertThatThrownBy`.
- When a test has more than one assertion, use `BDDSoftAssertions` via `@ExtendWith(SoftAssertionsExtension.class)` and `@InjectSoftAssertions`, replacing `then(...)` with `softly.then(...)`.
- When a test method would declare checked exceptions, use Lombok's `@SneakyThrows` instead of adding `throws` to the signature.

### Clean Code
- **SOLID**: single responsibility, open/closed, no god objects.
- **DRY**: extract shared logic; never copy-paste across classes or modules.
- **OOP**: encapsulate state, prefer instance methods over static utilities when state is involved.
- Small methods, descriptive names, no magic numbers or strings.
- No dead code.
- Use `final` on all fields and parameters where the value is not reassigned. Use `var` for local variables, unless doing so would require an explicit unchecked cast — in that case, declare the explicit type instead.
- Use 2-space indentation.
- Do not write comments that restate what the code already says. Only comment to explain a non-obvious decision or constraint that cannot be expressed in the code itself.

### Architecture
- `domain` layer must not depend on Spring or `infrastructure` — enforced at build time by ArchUnit.
- Domain rule violations must extend `DomainException`.
- Cross-aggregate coupling at infrastructure level is forbidden.

### Documentation
- `ARCHITECTURE.md` must always reflect the current state of the project.
- Update it when a module or package is added, renamed, or removed, when a key design decision changes, or when a new workflow or release mechanism is introduced.

---

## For interactive agents making code changes

### Commits
- **Never create git commits.** The user reviews changes and commits manually.
- Prepare each improvement as a clean, self-contained change ready to commit, then stop and wait.

### TDD process

Use the `tdd` skill for any implementation task: load it with the `skill` tool at the start of each coding session.

Apply TDD for all implementation work: domain logic, use cases, services, and infrastructure adapters.

Skip TDD for pure configuration that contains no logic: Spring `@Configuration` classes, `application.yml`, GraalVM hints, Dockerfile, and build descriptors.

### Before finishing
- Run `./mvnw spotless:apply` to auto-format all code.
- Run `./mvnw verify` — all checks must pass.
