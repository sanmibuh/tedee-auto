# Agent Guidelines

Instructions for AI assistants working on this project. Read this before making any changes.

Read `ARCHITECTURE.md` before making any changes to understand module design, data flow, and key decisions.

---

## Workflow

### Commits

- **Never create git commits.** The user reviews changes and commits manually.
- Prepare each improvement as a clean, self-contained change ready to commit, then stop and wait.

### TDD — tests first, always
1. Write the test and watch it fail before writing any implementation.
2. Implement the minimum code to make it pass.
3. Refactor with the tests as the safety net.

Never write implementation code without a corresponding test.

Use `BDDAssertions` from AssertJ in all tests (`then`, `thenThrownBy`). Never use `assertThat` or `assertThatThrownBy`.

When a test has more than one assertion, use `BDDSoftAssertions` via `@ExtendWith(SoftAssertionsExtension.class)` and `@InjectSoftAssertions`, replacing `then(...)` with `softly.then(...)`.

When a test method would declare checked exceptions, annotate it with Lombok's `@SneakyThrows` instead of adding `throws` to the signature.

Test method names use snake_case with camelCase segments: `should_doSomething_whenCondition`.

Do not write comments that restate what the code already says. Only comment when explaining a non-obvious decision or constraint that cannot be expressed in the code itself.

---

## Code quality

- **SOLID**: single responsibility, open/closed, no god objects.
- **Clean Code**: small methods, descriptive names, no magic numbers, no dead code.
- **OOP**: encapsulate state, prefer instance methods over static utilities when state is involved.
- **DRY**: extract shared logic; never copy-paste across classes or modules.
- Use `final` on all fields and parameters where the value is not reassigned. Use `var` for local variables.
- Use 2-space indentation.
- Run `./mvnw spotless:apply` to auto-format all code before considering any task done.
- Run `./mvnw verify` before considering any task done. All checks must pass.

---

## Documentation

### Update `ARCHITECTURE.md` when:

- A new module or package is added, renamed, or removed.
- A key design decision changes (e.g. data flow, dependency structure, configuration approach).
- A new workflow or release mechanism is introduced.
